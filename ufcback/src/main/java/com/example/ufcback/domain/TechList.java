package com.example.ufcback.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tech_list", schema = "ufc")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class TechList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "topic_keyword", nullable = false, length = 100, unique = true)
    private String topicKeyword;

    @Column(name = "primary_repo", length = 100)
    private String primaryRepo;

    @Column(name = "github_repo_id", unique = true)
    private Long githubRepoId;

    @Column(length = 20)
    private String color;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
