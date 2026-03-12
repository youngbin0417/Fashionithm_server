package com.example.fashionithm.influencer.controller;

import com.example.fashionithm.influencer.domain.Influencer;
import com.example.fashionithm.influencer.repository.InfluencerRepository;
import com.example.fashionithm.influencer.service.InstagramMonitoringService;
import com.example.fashionithm.influencer.service.MonitoringResult;
import com.example.fashionithm.influencer.service.PostMonitoringResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerRepository influencerRepository;
    private final InstagramMonitoringService monitoringService;

    /**
     * 모든 인플루언서 조회
     */
    @GetMapping
    public ResponseEntity<List<Influencer>> getAllInfluencers() {
        return ResponseEntity.ok(influencerRepository.findAll());
    }

    /**
     * 인플루언서 ID 로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Influencer> getInfluencer(@PathVariable Long id) {
        return influencerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 카테고리별 인플루언서 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Influencer>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(influencerRepository.findByCategory(category));
    }

    /**
     * 인스타그램 ID 로 조회
     */
    @GetMapping("/instagram/{instagramId}")
    public ResponseEntity<Influencer> getByInstagramId(@PathVariable String instagramId) {
        return influencerRepository.findByInstagramId(instagramId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 인플루언서 모니터링 실행
     */
    @PostMapping("/{instagramId}/monitor")
    public ResponseEntity<List<PostMonitoringResult>> monitorInfluencer(
            @PathVariable String instagramId) {
        List<PostMonitoringResult> results = monitoringService.monitorByInfluencer(instagramId);
        return ResponseEntity.ok(results);
    }

    /**
     * 전체 모니터링 실행
     */
    @PostMapping("/monitor-all")
    public ResponseEntity<MonitoringResult> monitorAll() {
        MonitoringResult result = monitoringService.monitorAll();
        return ResponseEntity.ok(result);
    }
}
