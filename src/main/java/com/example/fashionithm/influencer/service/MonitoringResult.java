package com.example.fashionithm.influencer.service;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MonitoringResult {
    private final int totalPosts;
    private final int totalBrandMentions;
    private final List<PostMonitoringResult> postResults;
    private final Map<String, Integer> brandMentionCount;

    @Builder
    public MonitoringResult(int totalPosts, int totalBrandMentions, 
                           List<PostMonitoringResult> postResults,
                           Map<String, Integer> brandMentionCount) {
        this.totalPosts = totalPosts;
        this.totalBrandMentions = totalBrandMentions;
        this.postResults = postResults;
        this.brandMentionCount = brandMentionCount;
    }
}
