package com.example.fashionithm.influencer.service;

import com.example.fashionithm.brand.domain.BrandMention;
import com.example.fashionithm.influencer.domain.Influencer;
import com.example.fashionithm.influencer.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostMonitoringResult {
    private final Post post;
    private final Influencer influencer;
    private final List<BrandMention> brandMentions;

    @Builder
    public PostMonitoringResult(Post post, Influencer influencer, List<BrandMention> brandMentions) {
        this.post = post;
        this.influencer = influencer;
        this.brandMentions = brandMentions;
    }
}
