package com.example.ufcback.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubSearchResponse(
    @JsonProperty("total_count")
    @PositiveOrZero Integer totalCount,
    
    @NotNull List<GitHubRepoItem> items
) {}
