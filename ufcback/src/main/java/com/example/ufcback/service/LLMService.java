package com.example.ufcback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final RestTemplate restTemplate;

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
        restTemplate.postForObject(url, null, String.class);
    }
}
