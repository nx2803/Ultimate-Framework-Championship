package com.example.ufcback.service;

import com.example.ufcback.controller.dto.TechStatsResponse;
import com.example.ufcback.domain.TechList;
import com.example.ufcback.domain.TechStats;
import com.example.ufcback.repository.TechListRepository;
import com.example.ufcback.repository.TechStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechChartService {

    private final TechStatsRepository techStatsRepository;
    private final TechListRepository techListRepository;

    @com.example.ufcback.config.LogExecutionTime
    @Cacheable(value = "techStats", key = "#techId + '-' + #days")
    public List<TechStatsResponse> getStatsByTech(Long techId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        int interval = getSamplingInterval(days);
        
        TechList targetTech = techListRepository.findById(techId)
                .orElseThrow(() -> new IllegalArgumentException("Tech not found: " + techId));
        
        // DB 레벨에서 샘플링된 데이터만 조회
        List<TechStats> sampledStats = techStatsRepository.findSampledStatsByTech(techId, startDate, interval);
        
        // DB에서 카테고리 합계를 직접 조회
        List<com.example.ufcback.repository.CategoryTotalProjection> categoryTotals = 
                techStatsRepository.findCategoryTotalByDate(targetTech.getCategory(), startDate);
        
        Map<LocalDateTime, Long> totalRepoCountByDate = categoryTotals.stream()
                .collect(Collectors.toMap(
                        p -> p.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        p -> p.getTotalRepoCount(),
                        (existing, replacement) -> existing + replacement
                ));
        
            try {
                return sampledStats.stream()
                        .map(stats -> {
                            try {
                                if (stats.getCollectedAt() == null || stats.getTech() == null) return null;
                                
                                LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                                Long totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0L);
                                Integer currentRepoCount = stats.getRepoCount() != null ? stats.getRepoCount() : 0;
                                
                                Double share = (totalInDate > 0) 
                                        ? (double) currentRepoCount / totalInDate * 100.0 
                                        : 0.0;
                                
                                return TechStatsResponse.builder()
                                        .techId(stats.getTech().getId())
                                        .techName(stats.getTech().getName())
                                        .collectedAt(stats.getCollectedAt())
                                        .starCount(stats.getStarCount() != null ? stats.getStarCount() : 0)
                                        .forkCount(stats.getForkCount() != null ? stats.getForkCount() : 0)
                                        .repoCount(currentRepoCount)
                                        .marketShare(Math.round(share * 100.0) / 100.0)
                                        .build();
                            } catch (Exception e) {
                                log.error("Error converting TechStats to DTO for techId {}: {}", techId, e.getMessage());
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .toList();
            } catch (Exception e) {
                log.error("CRITICAL error in getStatsByTech for techId {}: {}", techId, e.getMessage(), e);
                throw e;
            }
        }

    @com.example.ufcback.config.LogExecutionTime
    @Cacheable(value = "categoryStats", key = "#category + '-' + #days")
    public List<TechStatsResponse> getStatsByCategory(String category, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        int interval = getSamplingInterval(days);
        
        // DB 레벨에서 샘플링된 데이터만 조회
        List<TechStats> sampledStats = techStatsRepository.findSampledStatsByCategory(category, startDate, interval);
        
        // DB에서 카테고리 합계를 직접 조회
        List<com.example.ufcback.repository.CategoryTotalProjection> categoryTotals = 
                techStatsRepository.findCategoryTotalByDate(category, startDate);
        
        Map<LocalDateTime, Long> totalRepoCountByDate = categoryTotals.stream()
                .collect(Collectors.toMap(
                        p -> p.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        p -> p.getTotalRepoCount(),
                        (existing, replacement) -> existing + replacement
                ));
        
            try {
                return sampledStats.stream()
                        .map(stats -> {
                            try {
                                if (stats.getCollectedAt() == null || stats.getTech() == null) return null;
            
                                LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                                Long totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0L);
                                Integer currentRepoCount = stats.getRepoCount() != null ? stats.getRepoCount() : 0;
            
                                Double share = (totalInDate > 0) 
                                        ? (double) currentRepoCount / totalInDate * 100.0 
                                        : 0.0;
            
                                return TechStatsResponse.builder()
                                        .techId(stats.getTech().getId())
                                        .techName(stats.getTech().getName())
                                        .collectedAt(stats.getCollectedAt())
                                        .starCount(stats.getStarCount() != null ? stats.getStarCount() : 0)
                                        .forkCount(stats.getForkCount() != null ? stats.getForkCount() : 0)
                                        .repoCount(currentRepoCount)
                                        .marketShare(Math.round(share * 100.0) / 100.0)
                                        .build();
                            } catch (Exception e) {
                                log.error("Error converting TechStats to DTO in category {}: {}", category, e.getMessage());
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .sorted(Comparator.comparing(TechStatsResponse::collectedAt)
                                .thenComparing(TechStatsResponse::techName))
                        .toList();
            } catch (Exception e) {
                log.error("CRITICAL error in getStatsByCategory for category {}: {}", category, e.getMessage(), e);
                throw e;
            }
        }

    private int getSamplingInterval(int days) {
        if (days <= 3) return 1;
        if (days <= 7) return 2;
        if (days <= 30) return 4;
        return 12;
    }

    /**
     * @deprecated DB 레벨 샘플링 쿼리(findSampledStats...) 사용을 권장합니다.
     */
    @Deprecated
    private List<TechStats> filterBySampling(List<TechStats> stats, int days) {
        if (stats == null || stats.isEmpty()) return List.of();
        
        int interval;
        if (days <= 3) interval = 1;
        else if (days <= 7) interval = 2;
        else if (days <= 30) interval = 4;
        else interval = 12;
        
        if (interval == 1) return stats;
        
        return stats.stream()
                .filter(s -> s.getCollectedAt() != null && s.getCollectedAt().getHour() % interval == 0)
                .toList();
    }
}
