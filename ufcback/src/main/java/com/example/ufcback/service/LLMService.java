package com.example.ufcback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.ufcback.repository.TechListRepository;
import com.example.ufcback.repository.TechStatsRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final RestTemplate restTemplate;
    private final TechListRepository techListRepository;
    private final TechStatsRepository techStatsRepository;

    @Value("${ufc.llm.base-url}")
    private String llmBaseUrl;

    private static final List<String> CATEGORIES = List.of(
            "FRONTEND", "BACKEND", "AI_ML", "INFRA", "MOBILE", "DATABASE", "LANGUAGE"
    );

    /**
     * 모든 카테고리에 대해 LLM 분석을 요청합니다.
     */
    @Async
    public void analyzeAllCategories() {
        log.info("Starting LLM analysis for all categories with 20s interval to honor API limits...");

        long totalTechs = techListRepository.count();
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        long collectedTechs = techStatsRepository.findByCollectedAtAfter(threshold)
                .stream().map(s -> s.getTech().getId()).distinct().count();

        log.info("Data completeness check: {}/{} technologies have recent stats.", collectedTechs, totalTechs);
        
        if (collectedTechs == 0 && totalTechs > 0) {
            log.error("Aborting LLM analysis: No data collected in the recent batch run.");
            return;
        }

        if (collectedTechs < totalTechs) {
            log.warn("Some technologies are missing recent stats. Analysis will proceed with available data.");
        }
        for (int i = 0; i < CATEGORIES.size(); i++) {
            String category = CATEGORIES.get(i);
            try {
                analyzeCategory(category);
                
                // 마지막 카테고리가 아니면 20초 대기 (5 RPM 제한 준수)
                if (i < CATEGORIES.size() - 1) {
                    Thread.sleep(20000); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Analysis interrupted: {}", e.getMessage());
                break;
            } catch (Exception e) {
                log.error("Failed to analyze category {}: {}", category, e.getMessage());
            }
        }
        log.info("Finished all LLM analysis requests.");
    }

    private void analyzeCategory(String category) {
        String url = UriComponentsBuilder.fromUriString(llmBaseUrl)
                .path("/analysis/analyze")
                .queryParam("category", category)
                .toUriString();

        log.info("Calling LLM Analysis for category: {} (URL: {})", category, url);

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                restTemplate.postForObject(url, null, String.class);
                log.info("Successfully queued analysis for category: {}", category);
                break;
            } catch (Exception e) {
                log.warn("Attempt {}/{} failed for LLM Analysis of category {}: {}", attempt, maxRetries, category, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("All {} attempts failed for LLM Analysis of category {}", maxRetries, category);
                } else {
                    try { Thread.sleep(5000 * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }
    }
}
