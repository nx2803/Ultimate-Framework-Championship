package com.example.ufcback.controller;

import com.example.ufcback.domain.TechAnalysis;
import com.example.ufcback.repository.TechAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class TechAnalysisController {

    private final TechAnalysisRepository techAnalysisRepository;

    @GetMapping("/latest/{category}")
    public ResponseEntity<TechAnalysis> getLatestAnalysis(@PathVariable String category) {
        return techAnalysisRepository.findFirstByCategoryOrderByCreatedAtDesc(category)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
