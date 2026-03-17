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
        
        TechList targetTech = techListRepository.findById(techId)
                .orElseThrow(() -> new IllegalArgumentException("Tech not found: " + techId));
        
        List<TechStats> targetStats = techStatsRepository.findByTechIdAndCollectedAtAfterOrderByCollectedAtAsc(techId, startDate);
        
        List<TechStats> categoryStats = techStatsRepository.findByTechCategoryAndCollectedAtAfter(targetTech.getCategory(), startDate);
        
        Map<LocalDateTime, Integer> totalRepoCountByDate = categoryStats.stream()
                .filter(s -> s.getCollectedAt() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        Collectors.summingInt(s -> {
                            Integer rc = s.getRepoCount();
                            return rc != null ? rc : 0;
                        })
                ));
        
            try {
                return targetStats.stream()
                        .map(stats -> {
                            try {
                                if (stats.getCollectedAt() == null || stats.getTech() == null) return null;
                                
                                LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                                Integer totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0);
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
        
        List<TechStats> categoryStats = techStatsRepository.findByTechCategoryAndCollectedAtAfter(category, startDate);
        
        Map<LocalDateTime, Integer> totalRepoCountByDate = categoryStats.stream()
                .filter(s -> s.getCollectedAt() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getCollectedAt().withMinute(0).withSecond(0).withNano(0),
                        Collectors.summingInt(s -> {
                            Integer rc = s.getRepoCount();
                            return rc != null ? rc : 0;
                        })
                ));
        
            try {
                return categoryStats.stream()
                        .map(stats -> {
                            try {
                                if (stats.getCollectedAt() == null || stats.getTech() == null) return null;
            
                                LocalDateTime truncatedTime = stats.getCollectedAt().withMinute(0).withSecond(0).withNano(0);
                                Integer totalInDate = totalRepoCountByDate.getOrDefault(truncatedTime, 0);
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
}
