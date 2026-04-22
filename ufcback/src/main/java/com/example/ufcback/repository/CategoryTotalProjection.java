package com.example.ufcback.repository;

import java.time.LocalDateTime;

public interface CategoryTotalProjection {
    LocalDateTime getCollectedAt();
    Long getTotalRepoCount();
}
