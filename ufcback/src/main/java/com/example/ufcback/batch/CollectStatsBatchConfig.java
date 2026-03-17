package com.example.ufcback.batch;

import com.example.ufcback.domain.TechList;
import com.example.ufcback.domain.TechStats;
import com.example.ufcback.infrastructure.github.GitHubClient;
import com.example.ufcback.infrastructure.github.dto.GitHubRepoItem;
import com.example.ufcback.infrastructure.github.dto.GitHubSearchResponse;
import com.example.ufcback.repository.TechListRepository;
import com.example.ufcback.repository.TechStatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.Semaphore;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CollectStatsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TechListRepository techListRepository;
    private final TechStatsRepository techStatsRepository;
    private final GitHubClient gitHubClient;
    private final ObjectMapper objectMapper;
    private final Semaphore githubThrottler = new Semaphore(1);

    @Bean
    public Job collectStatsJob(Step collectStatsStep, Step clearCacheStep) {
        return new JobBuilder("collectStatsJob", jobRepository)
                .start(collectStatsStep)
                .next(clearCacheStep)
                .build();
    }

    @Bean
    public Step clearCacheStep(CacheManager cacheManager) {
        return new StepBuilder("clearCacheStep", jobRepository)
                .tasklet(clearCacheTasklet(cacheManager), transactionManager)
                .build();
    }

    @Bean
    public Tasklet clearCacheTasklet(CacheManager cacheManager) {
        return (contribution, chunkContext) -> {
            log.info("Evicting tech stats caches...");
            Cache techStatsCache = cacheManager.getCache("techStats");
            Cache categoryStatsCache = cacheManager.getCache("categoryStats");
            if (techStatsCache != null)
                techStatsCache.clear();
            if (categoryStatsCache != null)
                categoryStatsCache.clear();
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step collectStatsStep(ItemWriter<TechStats> techStatsWriter) {
        return new StepBuilder("collectStatsStep", jobRepository)
                .<TechList, TechStats>chunk(10, transactionManager)
                .reader(techListReader())
                .processor(techStatsProcessor())
                .writer(techStatsWriter)
                .taskExecutor(batchTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor batchTaskExecutor() {
        return new VirtualThreadTaskExecutor("batch-thread-");
    }

    @Bean
    public RepositoryItemReader<TechList> techListReader() {
        return new RepositoryItemReaderBuilder<TechList>()
                .name("techListReader")
                .repository(techListRepository)
                .methodName("findAll")
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @org.springframework.batch.core.configuration.annotation.StepScope
    public ItemProcessor<TechList, TechStats> techStatsProcessor() {
        final LocalDateTime batchTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        return tech -> {
            try {
                githubThrottler.acquire();
                try {
                    // Search API 30회/분 한도 (Primary/Search 2회 호출 시 기술당 2콜)
                    // 기존 2.5초에서 3초로 보수적으로 늘려 GitHub의 엄격한 제한을 우회
                    Thread.sleep(3000); 
                    log.info("Collecting stats for: {} (Repo: {}, Topic: {})",
                            tech.getName(), tech.getPrimaryRepo(), tech.getTopicKeyword());

                    Integer stars = 0;
                    Integer forks = 0;
                    if (tech.getPrimaryRepo() != null && !tech.getPrimaryRepo().isEmpty()) {
                        try {
                            String repoJson = gitHubClient.getRepositoryDetails(tech.getPrimaryRepo());
                            GitHubRepoItem repoItem = objectMapper.readValue(repoJson, GitHubRepoItem.class);

                            if (repoItem != null) {
                                Integer s = repoItem.stargazersCount();
                                Integer f = repoItem.forksCount();
                                stars = (s != null) ? s : 0;
                                forks = (f != null) ? f : 0;
                            }
                        } catch (Exception e) {
                            log.warn("Failed to get repo details for {}: {}", tech.getPrimaryRepo(), e.getMessage());
                        }
                    }

                    Integer repoCount = 0;
                    if (tech.getTopicKeyword() != null && !tech.getTopicKeyword().isEmpty()) {
                        try {
                            // LANGUAGE 카테고리는 language: 쿼리 사용 (topic: 쿼리보다 정확하고 안정적)
                            // 기타 카테고리는 topic: 쿼리 사용
                            boolean isLanguage = "LANGUAGE".equals(tech.getCategory());
                            String searchJson = isLanguage
                                    ? gitHubClient.getLanguageStats(tech.getTopicKeyword())
                                    : gitHubClient.getTopicStats(tech.getTopicKeyword());

                            GitHubSearchResponse searchResponse = objectMapper.readValue(searchJson,
                                    GitHubSearchResponse.class);

                            if (searchResponse != null && searchResponse.totalCount() != null) {
                                repoCount = searchResponse.totalCount();
                                log.info("[{}] {} query='{}{}': {} repos",
                                        tech.getCategory(), tech.getName(),
                                        isLanguage ? "language:" : "topic:",
                                        tech.getTopicKeyword(), repoCount);
                            }
                        } catch (Exception e) {
                            log.error("Failed to get repo count for {} ({}): {}",
                                    tech.getName(), tech.getCategory(), e.getMessage());
                        }
                    }

                    // Fallback: 만약 API 제한 등으로 인해 0이 수집되었다면 직전 성공 데이터를 재사용
                    if (stars == 0 || forks == 0 || repoCount == 0) {
                        try {
                            TechStats lastStats = techStatsRepository.findFirstByTechIdOrderByCollectedAtDesc(tech.getId()).orElse(null);
                            if (lastStats != null) {
                                log.warn("Fallback applied for {}. Reusing last known stats.", tech.getName());
                                stars = (stars == 0 && lastStats.getStarCount() != null) ? lastStats.getStarCount() : stars;
                                forks = (forks == 0 && lastStats.getForkCount() != null) ? lastStats.getForkCount() : forks;
                                repoCount = (repoCount == 0 && lastStats.getRepoCount() != null) ? lastStats.getRepoCount() : repoCount;
                            }
                        } catch (Exception e) {
                            log.error("Failed to load fallback stats for {}", tech.getName(), e);
                        }
                    }

                    return TechStats.builder()
                            .tech(tech)
                            .starCount(stars)
                            .forkCount(forks)
                            .repoCount(repoCount)
                            .collectedAt(batchTime)
                            .build();
                } finally {
                    githubThrottler.release();
                }
            } catch (Exception e) {
                log.error("CRITICAL error collecting stats for {}: {}", tech.getName(), e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<TechStats> techStatsWriter(TechStatsRepository repository) {
        return chunk -> {
            log.info("Writing chunk of {} items", chunk.size());
            for (TechStats stats : chunk) {
                repository.findByTechIdAndCollectedAt(stats.getTech().getId(), stats.getCollectedAt())
                        .ifPresentOrElse(
                                existing -> {
                                    existing.updateStats(stats.getStarCount(), stats.getForkCount(),
                                            stats.getRepoCount());
                                    repository.save(existing);
                                },
                                () -> repository.save(stats));
            }
        };
    }
}
