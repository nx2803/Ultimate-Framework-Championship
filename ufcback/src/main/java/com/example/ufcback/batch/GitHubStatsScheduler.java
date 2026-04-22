package com.example.ufcback.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class GitHubStatsScheduler {

    private final JobLauncher jobLauncher;
    private final Job collectStatsJob;
    private final Job trendingScanJob;
    private final com.example.ufcback.service.LLMService llmService;

    /**
     * TrendingScan: 매 정각 실행 (0분)
     * Search API 1회 사용
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runTrendingScan() {
        try {
            log.info("Starting TrendingScan at {}", LocalDateTime.now());
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("executedAt", LocalDateTime.now())
                    .toJobParameters();
            jobLauncher.run(trendingScanJob, params);
            log.info("TrendingScan finished");
        } catch (Exception e) {
            log.error("Failed to run TrendingScan: {}", e.getMessage());
        }
    }

    /**
     * CollectStats: 매일 새벽 00:30분에 실행 (GitHub star/fork/repo 수집)
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void runCollectStats() {
        try {
            log.info("Starting CollectStats at {}", LocalDateTime.now());
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("executedAt", LocalDateTime.now())
                    .toJobParameters();
            jobLauncher.run(collectStatsJob, params);
            log.info("CollectStats finished.");
        } catch (Exception e) {
            log.error("Failed to run CollectStats: {}", e.getMessage());
        }
    }

    /**
     * LLM Analysis: 매일 새벽 4시 30분에 실행 (AI 인사이트 분석, 하루 1회)
     */
    @Scheduled(cron = "0 30 4 * * *")
    public void runLLMAnalysis() {
        log.info("Starting daily LLM analysis at {}", LocalDateTime.now());
        llmService.analyzeAllCategories();
    }
}
