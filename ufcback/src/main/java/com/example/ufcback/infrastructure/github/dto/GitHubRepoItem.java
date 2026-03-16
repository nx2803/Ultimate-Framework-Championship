package com.example.ufcback.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoItem(
    @NotNull Long id,
    @NotBlank String name,
    
    @JsonProperty("full_name")
    @NotBlank String fullName,
    
    @JsonProperty("stargazers_count")
    @PositiveOrZero Integer stargazersCount,
    
    @JsonProperty("forks_count")
    @PositiveOrZero Integer forksCount,
    
    List<String> topics,
    
    String language
) {}
