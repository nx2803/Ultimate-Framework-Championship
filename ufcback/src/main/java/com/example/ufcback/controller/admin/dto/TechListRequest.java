package com.example.ufcback.controller.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechListRequest {
    private String name;
    private String category;
    private String topicKeyword;
    private Long githubRepoId;
}
