package com.example.fashionithm.brand.repository;

import com.example.fashionithm.brand.domain.Brand;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BrandRepository {

    private final Map<Long, Brand> brands = new HashMap<>();

    public BrandRepository() {
        // 하드코딩된 브랜드 데이터
        brands.put(1L, Brand.builder()
            .id(1L)
            .name("무신사")
            .category("패션 플랫폼")
            .keywords(new String[]{"무신사", "musinsa", "무신사스탠다드"})
            .build());

        brands.put(2L, Brand.builder()
            .id(2L)
            .name("자라")
            .category("패션")
            .keywords(new String[]{"자라", "zara", "ZARA"})
            .build());

        brands.put(3L, Brand.builder()
            .id(3L)
            .name("에이블리")
            .category("패션 플랫폼")
            .keywords(new String[]{"에이블리", "ably", "ABLY"})
            .build());

        brands.put(4L, Brand.builder()
            .id(4L)
            .name("유니클로")
            .category("패션")
            .keywords(new String[]{"유니클로", "uniqlo", "UNIQLO"})
            .build());

        brands.put(5L, Brand.builder()
            .id(5L)
            .name("H&M")
            .category("패션")
            .keywords(new String[]{"H&M", "hm", "HandM"})
            .build());

        brands.put(6L, Brand.builder()
            .id(6L)
            .name("나이키")
            .category("스포츠웨어")
            .keywords(new String[]{"나이키", "nike", "NIKE", "스우시"})
            .build());

        brands.put(7L, Brand.builder()
            .id(7L)
            .name("아디다스")
            .category("스포츠웨어")
            .keywords(new String[]{"아디다스", "adidas", "ADIDAS", "3 줄"})
            .build());

        brands.put(8L, Brand.builder()
            .id(8L)
            .name("COS")
            .category("패션")
            .keywords(new String[]{"COS", "코스"})
            .build());

        brands.put(9L, Brand.builder()
            .id(9L)
            .name("스타벅스")
            .category("카페")
            .keywords(new String[]{"스타벅스", "starbucks", "스타벅스"})
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
