package com.example.ufcback.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tech_stats", schema = "ufc", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"tech_id", "collected_at"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TechStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_id", nullable = false)
    private TechList tech;

    @Column(name = "star_count")
    private Integer starCount;

    @Column(name = "fork_count")
    private Integer forkCount;

    @Column(name = "repo_count")
    private Integer repoCount;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public void updateStats(Integer starCount, Integer forkCount, Integer repoCount) {
        this.starCount = starCount;
        this.forkCount = forkCount;
        this.repoCount = repoCount;
    }
}
