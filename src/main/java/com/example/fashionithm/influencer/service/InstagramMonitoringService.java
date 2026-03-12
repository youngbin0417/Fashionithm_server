package com.example.fashionithm.influencer.service;

import com.example.fashionithm.brand.domain.BrandMention;
import com.example.fashionithm.brand.service.BrandDetectionService;
import com.example.fashionithm.influencer.domain.Influencer;
import com.example.fashionithm.influencer.domain.Post;
import com.example.fashionithm.influencer.repository.InfluencerRepository;
import com.example.fashionithm.influencer.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramMonitoringService {

    private final InfluencerRepository influencerRepository;
    private final PostRepository postRepository;
    private final BrandDetectionService brandDetectionService;

    /**
     * 모든 인플루언서의 게시물 모니터링
     */
    public MonitoringResult monitorAll() {
        log.info("Starting Instagram monitoring...");

        List<Influencer> influencers = influencerRepository.findAll();
        List<PostMonitoringResult> postResults = new ArrayList<>();
        Map<String, Integer> brandMentionCount = new HashMap<>();

        for (Influencer influencer : influencers) {
            List<Post> posts = postRepository.findByInfluencerInstagramId(
                influencer.getInstagramId());

            for (Post post : posts) {
                List<BrandMention> mentions = brandDetectionService.detectBrands(post);

                // 브랜드 언급 카운트 집계
                for (BrandMention mention : mentions) {
                    brandMentionCount.merge(mention.getBrandName(), 1, Integer::sum);
                }

                postResults.add(PostMonitoringResult.builder()
                    .post(post)
                    .influencer(influencer)
                    .brandMentions(mentions)
                    .build());
            }
        }

        MonitoringResult result = MonitoringResult.builder()
            .totalPosts(postResults.size())
            .totalBrandMentions(brandMentionCount.values().stream().mapToInt(i -> i).sum())
            .postResults(postResults)
            .brandMentionCount(brandMentionCount)
            .build();

        log.info("Monitoring completed. Total posts: {}, Brand mentions: {}", 
            result.getTotalPosts(), result.getTotalBrandMentions());

        return result;
    }

    /**
     * 특정 인플루언서의 게시물 모니터링
     */
    public List<PostMonitoringResult> monitorByInfluencer(String instagramId) {
        Influencer influencer = influencerRepository.findByInstagramId(instagramId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Influencer not found: " + instagramId));

        List<Post> posts = postRepository.findByInfluencerInstagramId(instagramId);
        List<PostMonitoringResult> results = new ArrayList<>();

        for (Post post : posts) {
            List<BrandMention> mentions = brandDetectionService.detectBrands(post);
            results.add(PostMonitoringResult.builder()
                .post(post)
                .influencer(influencer)
                .brandMentions(mentions)
                .build());
        }

        return results;
    }

    /**
     * 최근 게시물 모니터링
     */
    public List<PostMonitoringResult> monitorRecent(int days) {
        List<Post> recentPosts = postRepository.findRecent(days);
        List<PostMonitoringResult> results = new ArrayList<>();

        for (Post post : recentPosts) {
            Influencer influencer = influencerRepository.findByInstagramId(
                    post.getInfluencerInstagramId())
                .orElse(null);

            List<BrandMention> mentions = brandDetectionService.detectBrands(post);
            results.add(PostMonitoringResult.builder()
                .post(post)
                .influencer(influencer)
                .brandMentions(mentions)
                .build());
        }

        return results;
    }

    /**
     * 브랜드별 언급 게시물 조회
     */
    public List<PostMonitoringResult> findByBrand(String brandName) {
        List<PostMonitoringResult> allResults = monitorAll().getPostResults();
        
        return allResults.stream()
            .filter(r -> r.getBrandMentions().stream()
                .anyMatch(m -> m.getBrandName().equalsIgnoreCase(brandName)))
            .toList();
    }
}
