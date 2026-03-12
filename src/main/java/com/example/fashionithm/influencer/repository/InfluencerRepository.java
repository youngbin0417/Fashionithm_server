package com.example.fashionithm.influencer.repository;

import com.example.fashionithm.influencer.domain.Influencer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InfluencerRepository {
    
    private final Map<Long, Influencer> influencers = new HashMap<>();

    public InfluencerRepository() {
        // Hardcoded influencer data
        influencers.put(1L, Influencer.builder()
            .id(1L)
            .name("Fashion Kim")
            .instagramId("fashion_kim")
            .followerCount(150000)
            .category("Fashion")
            .tier("macro")
            .build());

        influencers.put(2L, Influencer.builder()
            .id(2L)
            .name("Style Index")
            .instagramId("style_index")
            .followerCount(85000)
            .category("Fashion")
            .tier("macro")
            .build());

        influencers.put(3L, Influencer.builder()
            .id(3L)
            .name("Daily Look")
            .instagramId("daily_look")
            .followerCount(45000)
            .category("Fashion")
            .tier("micro")
            .build());

        influencers.put(4L, Influencer.builder()
            .id(4L)
            .name("Street Fashion")
            .instagramId("street_fashion")
            .followerCount(120000)
            .category("Street")
            .tier("macro")
            .build());

        influencers.put(5L, Influencer.builder()
            .id(5L)
            .name("Minimal Look")
            .instagramId("minimal_look")
            .followerCount(67000)
            .category("Minimal")
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
