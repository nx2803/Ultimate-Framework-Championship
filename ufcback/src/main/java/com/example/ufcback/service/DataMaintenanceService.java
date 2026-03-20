package com.example.ufcback.service;

import com.example.ufcback.repository.TechAnalysisRepository;
import com.example.ufcback.repository.TechStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataMaintenanceService {

    private final TechStatsRepository techStatsRepository;
    private final TechAnalysisRepository techAnalysisRepository;

    /**
     * 매일 자정(00:00:00)에 90일이 지난 오래된 데이터를 자동 삭제합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupOldData() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(90);
        log.info("Starting automated cleanup of data older than 90 days (older than {})", threshold);

        try {
            int deletedStatsCount = techStatsRepository.deleteByCollectedAtBefore(threshold);
            log.info("Successfully deleted {} old TechStats records.", deletedStatsCount);

            int deletedAnalysisCount = techAnalysisRepository.deleteByCreatedAtBefore(threshold);
            log.info("Successfully deleted {} old TechAnalysis records.", deletedAnalysisCount);
            
        } catch (Exception e) {
            log.error("Failed during automated data cleanup: {}", e.getMessage(), e);
        }
    }
}
