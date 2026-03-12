package com.example.fashionithm.influencer.repository;

import com.example.fashionithm.influencer.domain.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostRepository {

    private final Map<Long, Post> posts = new HashMap<>();

    public PostRepository() {
        // 하드코딩된 게시물 데이터
        posts.put(1L, Post.builder()
            .id(1L)
            .instagramId("post_001")
            .influencerInstagramId("fashion_kim")
            .caption("오늘의 OOTD #무신사 #자라 #데일리룩")
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
            .caption("주말 쇼핑룩 🛍️ #에이블리 #패션스타그램")
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
            .caption("겨울 코디 추천 ❄️ #유니클로 #H&M #겨울패션")
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
            .caption("강남 카페 투어 ☕ #스타벅스 #카페감성")
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
            .caption("출근룩 추천 👔 #무신사스탠다드 #오피스룩")
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
            .caption("홍대 거리 패션 🔥 #나이키 #아디다스 #스트릿패션")
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
            .caption("미니멀한 일상 🤍 #자라 #COS #미니멀룩")
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
