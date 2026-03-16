package com.example.ufcback.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
@Tag(name = "Admin Batch", description = "관리자 전용 배치 수동 실행 API")
public class AdminBatchController {

    private final JobLauncher jobLauncher;
    private final Job collectStatsJob;
    private final Job trendingScanJob;

    @PostMapping("/collect-stats")
    @Operation(summary = "기술 점유율 수집 배치 즉시 실행", description = "Github 정보를 조회하여 기술별 점유율 통계를 즉시 계산하고 DB에 저장합니다.")
    public ResponseEntity<String> runCollectStatsJob() {
        try {
            log.info("Manual CollectStatsJob triggered at {}", LocalDateTime.now());
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("executedAt", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(collectStatsJob, params);
            return ResponseEntity.ok("기술 점유율 수집 배치가 성공적으로 시작/실행되었습니다.");
        } catch (Exception e) {
            log.error("Failed to run manual CollectStatsJob", e);
            return ResponseEntity.internalServerError().body("배치 실행 실패: " + e.getMessage());
        }
    }

    @PostMapping("/trending-scan")
    @Operation(summary = "트렌딩 레포지토리 스캔 즉시 실행", description = "Github 트렌딩을 스캔하여 기술 후보군을 확인합니다.")
    public ResponseEntity<String> runTrendingScanJob() {
        try {
            log.info("Manual TrendingScanJob triggered at {}", LocalDateTime.now());
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("executedAt", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(trendingScanJob, params);
            return ResponseEntity.ok("트렌딩 스캔 배치가 성공적으로 시작/실행되었습니다.");
        } catch (Exception e) {
            log.error("Failed to run manual TrendingScanJob", e);
            return ResponseEntity.internalServerError().body("배치 실행 실패: " + e.getMessage());
        }
    }
}
