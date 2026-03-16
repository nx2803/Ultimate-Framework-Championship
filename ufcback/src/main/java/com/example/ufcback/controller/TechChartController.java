package com.example.ufcback.controller;

import com.example.ufcback.controller.dto.TechStatsResponse;
import com.example.ufcback.service.TechChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tech Chart API", description = "프론트엔드 차트 시각화용 API")
@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class TechChartController {

    private final TechChartService techChartService;

    @Operation(summary = "특정 카테고리에 속한 모든 기술의 최근 N일간 통계 데이터 조회 (비교용)")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TechStatsResponse>> getCategoryStats(
            @PathVariable String category,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(techChartService.getStatsByCategory(category, days));
    }

    @Operation(summary = "특정 기술의 최근 N일간 통계 데이터 조회")
    @GetMapping("/{techId}")
    public ResponseEntity<List<TechStatsResponse>> getTechStats(
            @PathVariable Long techId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(techChartService.getStatsByTech(techId, days));
    }
}
