package com.example.ufcback.controller.dto;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Builder
public record TechStatsResponse(
    @NotNull Long techId,
    @NotBlank String techName,
    @NotNull LocalDateTime collectedAt,
    @PositiveOrZero Integer starCount,
    @PositiveOrZero Integer forkCount,
    @PositiveOrZero Integer repoCount,
    @PositiveOrZero Double marketShare
) {}
