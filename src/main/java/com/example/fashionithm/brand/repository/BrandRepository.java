package com.example.fashionithm.brand.repository;

import com.example.fashionithm.brand.domain.Brand;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BrandRepository {

    private final Map<Long, Brand> brands = new HashMap<>();

    public BrandRepository() {
        // Hardcoded brand data
        brands.put(1L, Brand.builder()
            .id(1L)
            .name("Musinsa")
            .category("Fashion Platform")
            .keywords(new String[]{"musinsa", "무신사", "musinsastandard"})
            .build());

        brands.put(2L, Brand.builder()
            .id(2L)
            .name("Zara")
            .category("Fashion")
            .keywords(new String[]{"zara", "자라", "ZARA"})
            .build());

        brands.put(3L, Brand.builder()
            .id(3L)
            .name("Ably")
            .category("Fashion Platform")
            .keywords(new String[]{"ably", "에이블리", "ABLY"})
            .build());

        brands.put(4L, Brand.builder()
            .id(4L)
            .name("Uniqlo")
            .category("Fashion")
            .keywords(new String[]{"uniqlo", "유니클로", "UNIQLO"})
            .build());

        brands.put(5L, Brand.builder()
            .id(5L)
            .name("H&M")
            .category("Fashion")
            .keywords(new String[]{"H&M", "hm", "HandM"})
            .build());

        brands.put(6L, Brand.builder()
            .id(6L)
            .name("Nike")
            .category("Sportswear")
            .keywords(new String[]{"nike", "나이키", "NIKE", "swosh"})
            .build());

        brands.put(7L, Brand.builder()
            .id(7L)
            .name("Adidas")
            .category("Sportswear")
            .keywords(new String[]{"adidas", "아디다스", "ADIDAS", "3stripes"})
            .build());

        brands.put(8L, Brand.builder()
            .id(8L)
            .name("COS")
            .category("Fashion")
            .keywords(new String[]{"COS", "코스"})
            .build());

        brands.put(9L, Brand.builder()
            .id(9L)
            .name("Starbucks")
            .category("Cafe")
            .keywords(new String[]{"starbucks", "스타벅스", "starbucks"})
            .build());
    }

    public Optional<Brand> findById(Long id) {
        return Optional.ofNullable(brands.get(id));
    }

    public Optional<Brand> findByName(String name) {
        return brands.values().stream()
            .filter(b -> b.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<Brand> findAll() {
        return new ArrayList<>(brands.values());
    }

    public List<Brand> findByCategory(String category) {
        return brands.values().stream()
            .filter(b -> b.getCategory().equals(category))
            .toList();
    }

    public List<Brand> findByKeyword(String keyword) {
        return brands.values().stream()
            .filter(b -> Arrays.stream(b.getKeywords())
                .anyMatch(k -> k.equalsIgnoreCase(keyword)))
            .toList();
    }
}
