package com.example.ufcback.batch;

import com.example.ufcback.infrastructure.github.GitHubClient;
import com.example.ufcback.infrastructure.github.dto.GitHubRepoItem;
import com.example.ufcback.infrastructure.github.dto.GitHubSearchResponse;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrendingScanBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final GitHubClient gitHubClient;
    private final ObjectMapper objectMapper;

    @Bean
    public Job trendingScanJob(Step trendingScanStep) {
        return new JobBuilder("trendingScanJob", jobRepository)
                .start(trendingScanStep)
                .build();
    }

    @Bean
    public Step trendingScanStep() {
        return new StepBuilder("trendingScanStep", jobRepository)
                .<GitHubRepoItem, GitHubRepoItem>chunk(10, transactionManager)
                .reader(trendingRepoReader())
                .processor(trendingRepoProcessor())
                .writer(trendingRepoWriter())
                .build();
    }

    @Bean
    public org.springframework.batch.infrastructure.item.ItemReader<GitHubRepoItem> trendingRepoReader() {
        // ItemReader는 null 반환 시까지 반복 호출됨
        // → API는 딱 1번만 호출하고, 결과 리스트를 순서대로 반환해야 함
        // 이전 코드: 매 read() 호출마다 API 호출 → rate limit 소진될 때까지 무한 호출 버그
        final java.util.concurrent.atomic.AtomicReference<java.util.Iterator<GitHubRepoItem>> iteratorRef =
                new java.util.concurrent.atomic.AtomicReference<>(null);

        return () -> {
            // 첫 read() 호출 시에만 API 조회
            if (iteratorRef.get() == null) {
                try {
                    String json = gitHubClient.searchTrendingRepositories();
                    GitHubSearchResponse response = objectMapper.readValue(json, GitHubSearchResponse.class);
                    iteratorRef.set(response.items().iterator());
                } catch (Exception e) {
                    log.error("Failed to fetch trending repos: {}", e.getMessage());
                    iteratorRef.set(java.util.Collections.<GitHubRepoItem>emptyList().iterator());
                }
            }
            java.util.Iterator<GitHubRepoItem> it = iteratorRef.get();
            return (it != null && it.hasNext()) ? it.next() : null;
        };
    }

    @Bean
    public ItemProcessor<GitHubRepoItem, GitHubRepoItem> trendingRepoProcessor() {
        return item -> {
            log.info("Processing trending repo: {}", item.fullName());
            return item;
        };
    }

    @Bean
    public ItemWriter<GitHubRepoItem> trendingRepoWriter() {
        return chunk -> {
            for (GitHubRepoItem item : chunk) {
                log.info("Saving trending repo: {}", item.fullName());
                // Logic to save or update TechList based on trending results
            }
        };
    }
}
