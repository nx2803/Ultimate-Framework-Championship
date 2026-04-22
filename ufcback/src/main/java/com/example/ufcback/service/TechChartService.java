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
import java.util.ArrayList;
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
        List<LocalDateTime> targetTimes = generateTargetTimes(startDate, interval);
        
        TechList targetTech = techListRepository.findById(techId)
                .orElseThrow(() -> new IllegalArgumentException("Tech not found: " + techId));
        
        List<TechStats> sampledStats = techStatsRepository.findStatsByTechInTimes(techId, targetTimes);
        
        List<com.example.ufcback.repository.CategoryTotalProjection> categoryTotals = 
                techStatsRepository.findCategoryTotalByDate(targetTech.getCategory(), startDate);
        
        Map<LocalDateTime, Long> totalRepoCountByDate = categoryTotals.stream()
                .collect(Collectors.toMap(
                        p -> p.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        p -> p.getTotalRepoCount(),
                        (existing, replacement) -> existing + replacement
                ));
        
        return sampledStats.stream()
                .map(stats -> {
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
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @com.example.ufcback.config.LogExecutionTime
    @Cacheable(value = "categoryStats", key = "#category + '-' + #days")
    public List<TechStatsResponse> getStatsByCategory(String category, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        int interval = getSamplingInterval(days);
        List<LocalDateTime> targetTimes = generateTargetTimes(startDate, interval);
        
        List<TechStats> sampledStats = techStatsRepository.findStatsByCategoryInTimes(category, targetTimes);
        
        List<com.example.ufcback.repository.CategoryTotalProjection> categoryTotals = 
                techStatsRepository.findCategoryTotalByDate(category, startDate);
        
        Map<LocalDateTime, Long> totalRepoCountByDate = categoryTotals.stream()
                .collect(Collectors.toMap(
                        p -> p.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        p -> p.getTotalRepoCount(),
                        (existing, replacement) -> existing + replacement
                ));
        
        return sampledStats.stream()
                .map(stats -> {
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
                })
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(TechStatsResponse::collectedAt)
                        .thenComparing(TechStatsResponse::techName))
                .toList();
    }

    private List<LocalDateTime> generateTargetTimes(LocalDateTime startDate, int interval) {
        List<LocalDateTime> times = new ArrayList<>();
        LocalDateTime current = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        while (current.isAfter(startDate) || current.isEqual(startDate)) {
            if (current.getHour() % interval == 0) {
                times.add(current);
            }
            current = current.minusHours(1);
        }
        return times;
    }

    private int getSamplingInterval(int days) {
        if (days <= 3) return 1;
        if (days <= 7) return 2;
        if (days <= 30) return 4;
        return 12;
    }
}
