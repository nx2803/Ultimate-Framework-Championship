package com.example.ufcback.service;

import com.example.ufcback.controller.dto.TechStatsResponse;
import com.example.ufcback.domain.TechList;
import com.example.ufcback.domain.TechStats;
import com.example.ufcback.repository.TechListRepository;
import com.example.ufcback.repository.TechStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        TechList targetTech = techListRepository.findById(techId)
                .orElseThrow(() -> new IllegalArgumentException("Tech not found: " + techId));
        
        List<TechStats> targetStats = techStatsRepository.findByTechIdAndCollectedAtAfterOrderByCollectedAtAsc(techId, startDate);
        
        List<TechStats> categoryStats = techStatsRepository.findByTechCategoryAndCollectedAtAfter(targetTech.getCategory(), startDate);
        
        Map<LocalDateTime, Integer> totalRepoCountByDate = categoryStats.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        Collectors.summingInt(TechStats::getRepoCount)
                ));
        
        return targetStats.stream()
                .map(stats -> {
                    LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                    Integer totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0);
                    Double share = (totalInDate > 0) 
                            ? (double) stats.getRepoCount() / totalInDate * 100.0 
                            : 0.0;
                    
                    return TechStatsResponse.builder()
                            .techId(stats.getTech().getId())
                            .techName(stats.getTech().getName())
                            .collectedAt(stats.getCollectedAt())
                            .starCount(stats.getStarCount())
                            .forkCount(stats.getForkCount())
                            .repoCount(stats.getRepoCount())
                            .marketShare(Math.round(share * 100.0) / 100.0)
                            .build();
                })
                .toList();
    }

    @com.example.ufcback.config.LogExecutionTime
    @Cacheable(value = "categoryStats", key = "#category + '-' + #days")
    public List<TechStatsResponse> getStatsByCategory(String category, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<TechStats> categoryStats = techStatsRepository.findByTechCategoryAndCollectedAtAfter(category, startDate);
        
        Map<LocalDateTime, Integer> totalRepoCountByDate = categoryStats.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        Collectors.summingInt(TechStats::getRepoCount)
                ));
        
        return categoryStats.stream()
                .map(stats -> {
                    LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                    Integer totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0);
                    Double share = (totalInDate > 0) 
                            ? (double) stats.getRepoCount() / totalInDate * 100.0 
                            : 0.0;
                    
                    return TechStatsResponse.builder()
                            .techId(stats.getTech().getId())
                            .techName(stats.getTech().getName())
                            .collectedAt(stats.getCollectedAt())
                            .starCount(stats.getStarCount())
                            .forkCount(stats.getForkCount())
                            .repoCount(stats.getRepoCount())
                            .marketShare(Math.round(share * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(TechStatsResponse::collectedAt)
                        .thenComparing(TechStatsResponse::techName))
                .toList();
    }
}
