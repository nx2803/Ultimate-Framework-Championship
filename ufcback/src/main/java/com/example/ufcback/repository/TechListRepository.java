package com.example.ufcback.repository;

import com.example.ufcback.domain.TechList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TechListRepository extends JpaRepository<TechList, Long> {
    Optional<TechList> findByTopicKeyword(String topicKeyword);
    Optional<TechList> findByGithubRepoId(Long githubRepoId);
}
