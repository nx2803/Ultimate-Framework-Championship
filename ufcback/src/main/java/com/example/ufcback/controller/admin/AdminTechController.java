package com.example.ufcback.controller.admin;

import com.example.ufcback.domain.TechList;
import com.example.ufcback.service.TechAdminService;
import com.example.ufcback.controller.admin.dto.TechListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Tech Management", description = "관리자용 기술 목록 관리 API")
@RestController
@RequestMapping("/api/admin/techs")
@RequiredArgsConstructor
public class AdminTechController {

    private final TechAdminService techAdminService;

    @Operation(summary = "전체 기술 목록 조회")
    @GetMapping
    public ResponseEntity<List<TechList>> getAllTechs() {
        return ResponseEntity.ok(techAdminService.getAllTechs());
    }

    @Operation(summary = "신규 기술 추가")
    @PostMapping
    public ResponseEntity<TechList> addTech(@RequestBody TechListRequest request) {
        return ResponseEntity.ok(techAdminService.addTech(request));
    }

    @Operation(summary = "기술 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTech(@PathVariable Long id) {
        techAdminService.deleteTech(id);
        return ResponseEntity.noContent().build();
    }
}
