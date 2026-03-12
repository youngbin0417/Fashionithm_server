package com.example.fashionithm.brand.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BrandMention {
    private final Long brandId;
    private final String brandName;
    private final Long postId;
    private final double confidenceScore;
    private final String detectionMethod;
    private final String matchedKeyword;

    @Builder
    public BrandMention(Long brandId, String brandName, Long postId, 
                        double confidenceScore, String detectionMethod, String matchedKeyword) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.postId = postId;
        this.confidenceScore = confidenceScore;
        this.detectionMethod = detectionMethod;
        this.matchedKeyword = matchedKeyword;
    }
}
