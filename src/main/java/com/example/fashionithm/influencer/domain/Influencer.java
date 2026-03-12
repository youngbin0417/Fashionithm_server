package com.example.fashionithm.influencer.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Influencer {
    private final Long id;
    private final String name;
    private final String instagramId;
    private final int followerCount;
    private final String category;
    private final String tier;

    @Builder
    public Influencer(Long id, String name, String instagramId, int followerCount, 
                      String category, String tier) {
        this.id = id;
        this.name = name;
        this.instagramId = instagramId;
        this.followerCount = followerCount;
        this.category = category;
        this.tier = tier;
    }
}
