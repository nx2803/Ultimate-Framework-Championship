package com.example.ufcback.repository;

import com.example.ufcback.domain.TechStats;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TechStatsRepository extends JpaRepository<TechStats, Long> {
    List<TechStats> findByTechId(Long techId);
    // tech 연관 엔티티를 JOIN FETCH → N+1 쿼리 방지
    @EntityGraph(attributePaths = {"tech"})
    List<TechStats> findByTechIdAndCollectedAtAfterOrderByCollectedAtAsc(Long techId, LocalDateTime startDate);

    @EntityGraph(attributePaths = {"tech"})
    List<TechStats> findByTechCategoryAndCollectedAtAfter(String category, LocalDateTime startDate);

    List<TechStats> findByCollectedAtAfter(LocalDateTime startDate);
    Optional<TechStats> findByTechIdAndCollectedAt(Long techId, LocalDateTime collectedAt);
    
    // 수집 실패 시 이전(가장 최근) 데이터를 가져오기 위한 메서드
    Optional<TechStats> findFirstByTechIdOrderByCollectedAtDesc(Long techId);

    @Modifying
    @Query("DELETE FROM TechStats t WHERE t.collectedAt < :date")
    int deleteByCollectedAtBefore(@Param("date") LocalDateTime date);
}
