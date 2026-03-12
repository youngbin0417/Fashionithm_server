package com.example.fashionithm.brand.controller;

import com.example.fashionithm.brand.domain.Brand;
import com.example.fashionithm.brand.domain.BrandMention;
import com.example.fashionithm.brand.repository.BrandRepository;
import com.example.fashionithm.influencer.service.InstagramMonitoringService;
import com.example.fashionithm.influencer.service.PostMonitoringResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;
    private final InstagramMonitoringService monitoringService;

    /**
     * 모든 브랜드 조회
     */
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    /**
     * 브랜드 ID 로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrand(@PathVariable Long id) {
        return brandRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 카테고리별 브랜드 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Brand>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(brandRepository.findByCategory(category));
    }

    /**
     * 브랜드 언급 게시물 조회
     */
    @GetMapping("/{name}/mentions")
    public ResponseEntity<List<PostMonitoringResult>> getMentions(@PathVariable String name) {
        List<PostMonitoringResult> results = monitoringService.findByBrand(name);
        return ResponseEntity.ok(results);
    }

    /**
     * 브랜드 키워드 검색
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Brand>> searchByKeyword(@PathVariable String keyword) {
        List<Brand> brands = brandRepository.findByKeyword(keyword);
        return ResponseEntity.ok(brands);
    }
}
