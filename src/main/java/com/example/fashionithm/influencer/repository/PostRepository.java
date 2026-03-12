package com.example.fashionithm.influencer.repository;

import com.example.fashionithm.influencer.domain.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostRepository {

    private final Map<Long, Post> posts = new HashMap<>();

    public PostRepository() {
        // Hardcoded post data
        posts.put(1L, Post.builder()
            .id(1L)
            .instagramId("post_001")
            .influencerInstagramId("fashion_kim")
            .caption("Today's OOTD #musinsa #zara #dailylook")
            .imageUrl("https://example.com/image1.jpg")
            .likeCount(3420)
            .commentCount(156)
            .postedAt(LocalDateTime.now().minusDays(1))
            .mediaType("IMAGE")
            .build());

        posts.put(2L, Post.builder()
            .id(2L)
            .instagramId("post_002")
            .influencerInstagramId("fashion_kim")
            .caption("Weekend shopping look #ably #fashionstyle")
            .imageUrl("https://example.com/image2.jpg")
            .likeCount(5230)
            .commentCount(289)
            .postedAt(LocalDateTime.now().minusDays(3))
            .mediaType("IMAGE")
            .build());

        posts.put(3L, Post.builder()
            .id(3L)
            .instagramId("post_003")
            .influencerInstagramId("style_index")
            .caption("Winter coat recommendation #uniqlo #H&M #winterfashion")
            .imageUrl("https://example.com/image3.jpg")
            .likeCount(2890)
            .commentCount(134)
            .postedAt(LocalDateTime.now().minusDays(2))
            .mediaType("CAROUSEL")
            .build());

        posts.put(4L, Post.builder()
            .id(4L)
            .instagramId("post_004")
            .influencerInstagramId("style_index")
            .caption("Gangnam cafe tour #starbucks #cafevibe")
            .imageUrl("https://example.com/image4.jpg")
            .likeCount(1560)
            .commentCount(78)
            .postedAt(LocalDateTime.now().minusDays(5))
            .mediaType("IMAGE")
            .build());

        posts.put(5L, Post.builder()
            .id(5L)
            .instagramId("post_005")
            .influencerInstagramId("daily_look")
            .caption("Office look recommendation #musinsastandard #officelook")
            .imageUrl("https://example.com/image5.jpg")
            .likeCount(4120)
            .commentCount(201)
            .postedAt(LocalDateTime.now().minusDays(1))
            .mediaType("IMAGE")
            .build());

        posts.put(6L, Post.builder()
            .id(6L)
            .instagramId("post_006")
            .influencerInstagramId("street_fashion")
            .caption("Hongdae street fashion #nike #adidas #streetstyle")
            .imageUrl("https://example.com/image6.jpg")
            .likeCount(6780)
            .commentCount(345)
            .postedAt(LocalDateTime.now().minusDays(2))
            .mediaType("VIDEO")
            .build());

        posts.put(7L, Post.builder()
            .id(7L)
            .instagramId("post_007")
            .influencerInstagramId("minimal_look")
            .caption("Minimal daily life #zara #COS #minimalstyle")
            .imageUrl("https://example.com/image7.jpg")
            .likeCount(3450)
            .commentCount(167)
            .postedAt(LocalDateTime.now().minusDays(4))
            .mediaType("CAROUSEL")
            .build());
    }

    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Optional<Post> findByInstagramId(String instagramId) {
        return posts.values().stream()
            .filter(p -> p.getInstagramId().equals(instagramId))
            .findFirst();
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    public List<Post> findByInfluencerInstagramId(String instagramId) {
        return posts.values().stream()
            .filter(p -> p.getInfluencerInstagramId().equals(instagramId))
            .toList();
    }

    public List<Post> findRecent(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return posts.values().stream()
            .filter(p -> p.getPostedAt().isAfter(cutoff))
            .sorted((a, b) -> b.getPostedAt().compareTo(a.getPostedAt()))
            .toList();
    }
}
