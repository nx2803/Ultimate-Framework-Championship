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

    @Query("SELECT t.collectedAt as collectedAt, SUM(t.repoCount) as totalRepoCount " +
           "FROM TechStats t JOIN t.tech l " +
           "WHERE l.category = :category AND t.collectedAt >= :startDate " +
           "GROUP BY t.collectedAt")
    List<CategoryTotalProjection> findCategoryTotalByDate(@Param("category") String category, @Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT * FROM ufc.tech_stats t " +
           "WHERE t.tech_id = :techId AND t.collected_at >= :startDate " +
           "AND EXTRACT(HOUR FROM t.collected_at)::int % :interval = 0 " +
           "ORDER BY t.collected_at ASC", nativeQuery = true)
    List<TechStats> findSampledStatsByTech(@Param("techId") Long techId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("interval") int interval);

    @Query(value = "SELECT t.* FROM ufc.tech_stats t " +
           "JOIN ufc.tech_list l ON t.tech_id = l.id " +
           "WHERE l.category = :category AND t.collected_at >= :startDate " +
           "AND EXTRACT(HOUR FROM t.collected_at)::int % :interval = 0 " +
           "ORDER BY t.collected_at ASC", nativeQuery = true)
    List<TechStats> findSampledStatsByCategory(@Param("category") String category, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("interval") int interval);
}
