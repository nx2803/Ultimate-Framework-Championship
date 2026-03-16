package com.example.ufcback.service;

import com.example.ufcback.domain.TechList;
import com.example.ufcback.repository.TechListRepository;
import com.example.ufcback.controller.admin.dto.TechListRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechAdminService {

    private final TechListRepository techListRepository;

    public List<TechList> getAllTechs() {
        return techListRepository.findAll();
    }

    @Transactional
    public TechList addTech(TechListRequest request) {
        TechList tech = TechList.builder()
                .name(request.getName())
                .category(request.getCategory())
                .topicKeyword(request.getTopicKeyword())
                .githubRepoId(request.getGithubRepoId())
                .build();
        return techListRepository.save(tech);
    }

    @Transactional
    public void deleteTech(Long id) {
        techListRepository.deleteById(id);
    }
}
