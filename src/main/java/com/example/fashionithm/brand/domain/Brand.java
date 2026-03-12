package com.example.fashionithm.brand.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Brand {
    private final Long id;
    private final String name;
    private final String category;
    private final String[] keywords;

    @Builder
    public Brand(Long id, String name, String category, String[] keywords) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.keywords = keywords;
    }
}
