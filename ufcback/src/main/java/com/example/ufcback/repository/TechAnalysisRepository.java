package com.example.ufcback.repository;

import com.example.ufcback.domain.TechAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TechAnalysisRepository extends JpaRepository<TechAnalysis, Long> {
    Optional<TechAnalysis> findFirstByCategoryOrderByCreatedAtDesc(String category);
}
