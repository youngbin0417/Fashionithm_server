package com.example.fashionithm.brand.service;

import com.example.fashionithm.brand.domain.Brand;
import com.example.fashionithm.brand.domain.BrandMention;
import com.example.fashionithm.brand.repository.BrandRepository;
import com.example.fashionithm.influencer.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandDetectionService {

    private final BrandRepository brandRepository;

    /**
     * 게시물에서 브랜드 언급 감지
     */
    public List<BrandMention> detectBrands(Post post) {
        List<BrandMention> mentions = new ArrayList<>();
        String caption = post.getCaption();
        String[] words = caption.split("[\\s#]+");

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            // 브랜드 키워드 매칭
            List<Brand> matchingBrands = brandRepository.findByKeyword(word);

            for (Brand brand : matchingBrands) {
                double confidence = calculateConfidence(caption, word, brand);
                
                BrandMention mention = BrandMention.builder()
                    .brandId(brand.getId())
                    .brandName(brand.getName())
                    .postId(post.getId())
                    .confidenceScore(confidence)
                    .detectionMethod("KEYWORD_MATCH")
                    .matchedKeyword(word)
                    .build();

                mentions.add(mention);
            }
        }

        return mentions;
    }

    /**
     * 신뢰도 점수 계산
     */
    private double calculateConfidence(String caption, String matchedWord, Brand brand) {
        double baseScore = 0.5;

        // 해시태그로 언급된 경우 가중치 상승
        if (caption.contains("#" + matchedWord)) {
            baseScore += 0.2;
        }

        // 브랜드 공식 키워드 목록에 있는 경우 가중치 상승
        if (Arrays.stream(brand.getKeywords())
            .anyMatch(k -> k.equalsIgnoreCase(matchedWord))) {
            baseScore += 0.15;
        }

        // 캡션에서 여러 번 언급된 경우 가중치 상승
        int mentionCount = countOccurrences(caption.toLowerCase(), matchedWord.toLowerCase());
        if (mentionCount > 1) {
            baseScore += Math.min(0.15, (mentionCount - 1) * 0.05);
        }

        return Math.min(baseScore, 1.0);
    }

    private int countOccurrences(String text, String search) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(search, index)) != -1) {
            count++;
            index += search.length();
        }
        return count;
    }

    /**
     * 신뢰도 임계값 이상의 언급만 필터링
     */
    public List<BrandMention> filterByConfidence(List<BrandMention> mentions, double threshold) {
        return mentions.stream()
            .filter(m -> m.getConfidenceScore() >= threshold)
            .toList();
    }
}
