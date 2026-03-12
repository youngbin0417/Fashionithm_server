package com.example.fashionithm.influencer.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Post {
    private final Long id;
    private final String instagramId;
    private final String influencerInstagramId;
    private final String caption;
    private final String imageUrl;
    private final int likeCount;
    private final int commentCount;
    private final LocalDateTime postedAt;
    private final String mediaType;

    @Builder
    public Post(Long id, String instagramId, String influencerInstagramId, String caption,
                String imageUrl, int likeCount, int commentCount, LocalDateTime postedAt, 
                String mediaType) {
        this.id = id;
        this.instagramId = instagramId;
        this.influencerInstagramId = influencerInstagramId;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postedAt = postedAt;
        this.mediaType = mediaType;
    }
}
