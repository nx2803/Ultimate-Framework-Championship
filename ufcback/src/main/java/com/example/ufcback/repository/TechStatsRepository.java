package com.example.ufcback.repository;

import com.example.ufcback.domain.TechStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TechStatsRepository extends JpaRepository<TechStats, Long> {
    List<TechStats> findByTechId(Long techId);
    List<TechStats> findByTechIdAndCollectedAtAfterOrderByCollectedAtAsc(Long techId, LocalDateTime startDate);
    List<TechStats> findByTechCategoryAndCollectedAtAfter(String category, LocalDateTime startDate);
    List<TechStats> findByCollectedAtAfter(LocalDateTime startDate);
    Optional<TechStats> findByTechIdAndCollectedAt(Long techId, LocalDateTime collectedAt);
}
