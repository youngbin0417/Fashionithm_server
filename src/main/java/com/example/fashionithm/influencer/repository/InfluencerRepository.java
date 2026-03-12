package com.example.fashionithm.influencer.repository;

import com.example.fashionithm.influencer.domain.Influencer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InfluencerRepository {
    
    private final Map<Long, Influencer> influencers = new HashMap<>();

    public InfluencerRepository() {
        // 하드코딩된 인플루언서 데이터
        influencers.put(1L, Influencer.builder()
            .id(1L)
            .name("김패션")
            .instagramId("fashion_kim")
            .followerCount(150000)
            .category("패션")
            .tier("macro")
            .build());

        influencers.put(2L, Influencer.builder()
            .id(2L)
            .name("스타일지수")
            .instagramId("style_index")
            .followerCount(85000)
            .category("패션")
            .tier("macro")
            .build());

        influencers.put(3L, Influencer.builder()
            .id(3L)
            .name("데일리룩")
            .instagramId("daily_look")
            .followerCount(45000)
            .category("패션")
            .tier("micro")
            .build());

        influencers.put(4L, Influencer.builder()
            .id(4L)
            .name("스트릿패션")
            .instagramId("street_fashion")
            .followerCount(120000)
            .category("스트릿")
            .tier("macro")
            .build());

        influencers.put(5L, Influencer.builder()
            .id(5L)
            .name("미니멀룩")
            .instagramId("minimal_look")
            .followerCount(67000)
            .category("미니멀")
            .tier("micro")
            .build());
    }

    public Optional<Influencer> findById(Long id) {
        return Optional.ofNullable(influencers.get(id));
    }

    public Optional<Influencer> findByInstagramId(String instagramId) {
        return influencers.values().stream()
            .filter(i -> i.getInstagramId().equals(instagramId))
            .findFirst();
    }

    public List<Influencer> findAll() {
        return new ArrayList<>(influencers.values());
    }

    public List<Influencer> findByCategory(String category) {
        return influencers.values().stream()
            .filter(i -> i.getCategory().equals(category))
            .toList();
    }
}
