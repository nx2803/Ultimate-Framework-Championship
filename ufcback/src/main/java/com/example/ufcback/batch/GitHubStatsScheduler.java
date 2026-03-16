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

    /**
     * 수집 주기: 1시간마다 실행 (매 정각)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runDailyJobs() {
        try {
            log.info("Starting Daily Jobs at {}", LocalDateTime.now());
            
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("executedAt", LocalDateTime.now())
                    .toJobParameters();
            
            jobLauncher.run(trendingScanJob, params);
            jobLauncher.run(collectStatsJob, params);
            
            log.info("Successfully finished Daily Jobs");
        } catch (Exception e) {
            log.error("Failed to run Daily Jobs: {}", e.getMessage());
        }
    }
}
