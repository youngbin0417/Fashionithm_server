package com.example.fashionithm.influencer.controller;

import com.example.fashionithm.influencer.domain.Post;
import com.example.fashionithm.influencer.repository.PostRepository;
import com.example.fashionithm.influencer.service.InstagramMonitoringService;
import com.example.fashionithm.influencer.service.PostMonitoringResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final InstagramMonitoringService monitoringService;

    /**
     * 모든 게시물 조회
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    /**
     * 게시물 ID 로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return postRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 인플루언서의 게시물 조회
     */
    @GetMapping("/influencer/{instagramId}")
    public ResponseEntity<List<Post>> getByInfluencer(@PathVariable String instagramId) {
        List<Post> posts = postRepository.findByInfluencerInstagramId(instagramId);
        return ResponseEntity.ok(posts);
    }

    /**
     * 최근 게시물 조회
     */
    @GetMapping("/recent/{days}")
    public ResponseEntity<List<Post>> getRecentPosts(@PathVariable int days) {
        List<Post> posts = postRepository.findRecent(days);
        return ResponseEntity.ok(posts);
    }

    /**
     * 게시물 브랜드 감지
     */
    @GetMapping("/{id}/detect")
    public ResponseEntity<List<PostMonitoringResult>> detectBrands(@PathVariable Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
        
        List<PostMonitoringResult> result = monitoringService.monitorByInfluencer(
            post.getInfluencerInstagramId());
        
        return ResponseEntity.ok(result.stream()
            .filter(r -> r.getPost().getId().equals(id))
            .toList());
    }
}
