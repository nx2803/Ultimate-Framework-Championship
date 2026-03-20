package com.example.ufcback.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ufcback.domain.TechAnalysis;

import java.time.LocalDateTime;

public interface TechAnalysisRepository extends JpaRepository<TechAnalysis, Long> {
    Optional<TechAnalysis> findFirstByCategoryOrderByCreatedAtDesc(String category);

    @Modifying
    @Query("DELETE FROM TechAnalysis t WHERE t.createdAt < :date")
    int deleteByCreatedAtBefore(@Param("date") LocalDateTime date);
}
