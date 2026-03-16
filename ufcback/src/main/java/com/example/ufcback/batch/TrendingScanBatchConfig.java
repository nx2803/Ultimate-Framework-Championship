package com.example.ufcback.batch;

import com.example.ufcback.domain.TechList;
import com.example.ufcback.infrastructure.github.GitHubClient;
import com.example.ufcback.infrastructure.github.dto.GitHubRepoItem;
import com.example.ufcback.infrastructure.github.dto.GitHubSearchResponse;
import com.example.ufcback.repository.TechListRepository;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrendingScanBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TechListRepository techListRepository;
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
        return () -> {
            try {
                String json = gitHubClient.searchTrendingRepositories();
                GitHubSearchResponse response = objectMapper.readValue(json, GitHubSearchResponse.class);
                return response.items().isEmpty() ? null : response.items().get(0); // Simplification for now
            } catch (Exception e) {
                log.error("Failed to read trending repos: {}", e.getMessage());
                return null;
            }
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
