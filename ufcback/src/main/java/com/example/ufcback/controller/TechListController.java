package com.example.ufcback.controller;

import com.example.ufcback.domain.TechList;
import com.example.ufcback.repository.TechListRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tech List API", description = "공용 기술 목록 조회 API")
@RestController
@RequestMapping("/api/techs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TechListController {

    private final TechListRepository techListRepository;

    @Operation(summary = "전체 기술 목록 조회 (차트 선택용)")
    @GetMapping
    public List<TechList> getAllTechs() {
        return techListRepository.findAll();
    }
}
