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
import org.springframework.context.annotation.Bean;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    private final javax.sql.DataSource dataSource; // JobRepository 설정을 위해 추가
    private final Semaphore githubThrottler = new Semaphore(1);

    /**
     * PostgreSQL에서 멀티스레드 배치 작업 시 발생하는 'could not serialize access due to concurrent update' 오류 해결.
     * 격리 수준을 ISOLATION_READ_COMMITTED로 설정하여 공통 메타데이터 테이블 업데이트 충돌을 방지합니다.
     */
    @Bean
    @Primary
    public JobRepository jobRepository() throws Exception {
        org.springframework.batch.core.repository.support.JobRepositoryFactoryBean factory = new org.springframework.batch.core.repository.support.JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

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
                .<TechList, TechStats>chunk(20, transactionManager) // 청크 사이즈를 20으로 상향하여 메타데이터 업데이트 빈도 감소
                .reader(techListReader())
                .processor(techStatsProcessor())
                .writer(techStatsWriter)
                .build();
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
                    // Search API 30회/분 한도 고려 (기술당 2콜 시 분당 20~30콜 임계치)
                    // 기존 3초에서 5초로 상향하여 GitHub Secondary Rate Limit을 더 안전하게 회피
                    Thread.sleep(5000); 
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
                        } catch (java.io.IOException | RuntimeException e) {
                            log.warn("Failed to get repo details for {}: {}. Error: {}", tech.getPrimaryRepo(), e.getMessage(), e.getClass().getSimpleName());
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
                        } catch (java.io.IOException | RuntimeException e) {
                            log.error("Failed to get repo count for {} ({}): {}. Error: {}",
                                    tech.getName(), tech.getCategory(), e.getMessage(), e.getClass().getSimpleName());
                        }
                    }

                    // Fallback: API 실패나 제한으로 인해 0이 수집되었다면 직전 성공 데이터를 재사용
                    // 만약 직전 데이터도 0이라면(초기화 안됨), 수집을 건너뛰거나(null 리턴) 기본값 처리
                    if (stars == 0 || forks == 0 || repoCount == 0) {
                        try {
                            TechStats lastStats = techStatsRepository.findFirstByTechIdOrderByCollectedAtDesc(tech.getId()).orElse(null);
                            if (lastStats != null) {
                                log.warn("Fallback applied for {}. Original: (S:{}, F:{}, R:{}) -> Reusing: (S:{}, F:{}, R:{})", 
                                        tech.getName(), stars, forks, repoCount,
                                        lastStats.getStarCount(), lastStats.getForkCount(), lastStats.getRepoCount());
                                
                                stars = (stars == 0 && lastStats.getStarCount() != null && lastStats.getStarCount() > 0) ? lastStats.getStarCount() : stars;
                                forks = (forks == 0 && lastStats.getForkCount() != null && lastStats.getForkCount() > 0) ? lastStats.getForkCount() : forks;
                                repoCount = (repoCount == 0 && lastStats.getRepoCount() != null && lastStats.getRepoCount() > 0) ? lastStats.getRepoCount() : repoCount;
                            } else {
                                log.error("Critical: No previous stats found for {}. Cannot fallback from 0.", tech.getName());
                            }
                        } catch (Exception e) {
                            log.error("Failed to load fallback stats for {}", tech.getName(), e);
                        }
                    }

                    // 모든 시도 후에도 핵심 데이터(별점)가 0인 경우, 데이터 품질을 위해 수집 무시
                    if (stars == 0 && tech.getPrimaryRepo() != null) {
                        log.error("Skipping stats record for {} because stars are 0 after API attempt and fallback.", tech.getName());
                        return null;
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
            } catch (InterruptedException | RuntimeException e) {
                log.error("CRITICAL error collecting stats for {}: {}. Error: {}", tech.getName(), e.getMessage(), e.getClass().getSimpleName());
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
