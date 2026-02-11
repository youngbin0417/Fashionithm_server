## 1. 핵심 아키텍처 개선사항

### 1.1 Instagram API 전략 변경 (2024년 12월 이후)
- **Basic Display API → Graph API 마이그레이션** 
- **핵심 제약**: 개인 계정 불가, 비즈니스/크리에이터 계정만 지원
- **Rate Limit**: 계정당 시간당 200요청, `X-Business-Use-Case-Usage` 헤더 모니터링 필수 
- **해결책**: 인플루언서는 반드시 비즈니스 계정으로 전환 필요, 폴링 간격 최적화 (5분 → 15분)

### 1.2 Neo4j 5.x 데이터 모델 최적화
- **레이블 전략**: 특정 관계 타입 사용으로 traversal 최적화 
- **중간 노드 패턴**: 협찬 관계에 시간/신뢰도 속성을 위한 intermediate node 도입
- **인덱스**: `Influencer(instagram_id)`, `User(user_id)`, `Brand(name)`에 constraint + index

---

## 2. 상세 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              클라이언트 계층                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────┐  │
│  │   관리자 대시보드     │  │   사용자 웹앱         │  │   모바일 웹뷰     │  │
│  │   (Thymeleaf + HTMX) │  │   (Thymeleaf + Vue3) │  │   (PWA)          │  │
│  └──────────┬───────────┘  └──────────┬───────────┘  └────────┬─────────┘  │
└─────────────┼─────────────────────────┼───────────────────────┼────────────┘
              │                         │                       │
              └─────────────────────────┼───────────────────────┘
                                        │ HTTPS/WSS
┌───────────────────────────────────────▼─────────────────────────────────────┐
│                         API Gateway (Spring Cloud Gateway)                  │
│                    • Rate Limiting • JWT 검증 • 라우팅                         │
└───────────────────────────────────────┬─────────────────────────────────────┘
                                        │
        ┌───────────────────────────────┼───────────────────────────────┐
        │                               │                               │
┌───────▼────────┐          ┌───────────▼──────────┐        ┌───────────▼────────┐
│   사용자 서비스  │          │   핵심 비즈니스 서비스  │        │   알림 서비스       │
│  (Spring Boot) │          │    (Spring Boot)      │        │  (WebSocket/STOMP) │
│                │          │                       │        │                    │
│ • 인증/권한     │          │ • 인플루언서 관리       │        │ • 실시간 알림       │
│ • OAuth 처리   │          │ • 브랜드 감지          │        │ • SSE Fallback    │
│ • 토큰 관리    │          │ • 추천 엔진            │        │                    │
└────────────────┘          └───────────┬───────────┘        └────────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
           ┌────────▼────────┐ ┌────────▼────────┐ ┌────────▼────────┐
           │  인플루언서       │ │  추천 엔진       │ │  외부 연동       │
           │  도메인         │ │  도메인          │ │  도메인         │
           │                 │ │                 │ │                 │
           │ • 게시물 수집    │ │ • Graph-based   │ │ • 무신사 API    │
           │ • Rate Limit 관리│ │   CF           │ │ • 에이블리 API  │
           │ • Webhook 처리   │ │ • 랭킹 알고리즘  │ │ • 결제 연동     │
           └────────┬────────┘ └─────────────────┘ └─────────────────┘
                    │
┌───────────────────▼─────────────────────────────────────────────────────────┐
│                              메시징 계층 (Kafka)                             │
├─────────────────────────────────────────────────────────────────────────────┤
│  Topic: instagram.post.raw        → 원본 게시물 수집                          │
│  Topic: brand.detection.req       → 브랜드 감지 요청                          │
│  Topic: brand.detection.result    → 브랜드 감지 결과 (신뢰도 포함)            │
│  Topic: graph.update.cmd          → Neo4j 업데이트 명령                       │
│  Topic: recommendation.trigger    → 추천 계산 트리거                          │
│  Topic: user.notification         → 사용자 알림                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                        │
┌───────────────────────────────────────▼─────────────────────────────────────┐
│                              데이터 계층                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────┐  ┌─────────────────────────────────────┐  │
│  │      Neo4j 5.x (Cluster)    │  │      PostgreSQL 16                  │  │
│  │                             │  │                                     │  │
│  │  • 인플루언서-브랜드 그래프   │  │  • 사용자 계정/인증 정보             │  │
│  │  • 팔로우 관계              │  │  • 제품 메타데이터 캐시              │  │
│  │  • 협찬 관계 (시간 가중치)   │  │  • 클릭/구매 이력                   │  │
│  │  • 실시간 추천 쿼리         │  │  • 관리자 설정                      │  │
│  │                             │  │                                     │  │
│  │  [Core Servers] ←→ [Read Replicas]  │  [Primary] ←→ [Standby]      │  │
│  └─────────────────────────────┘  └─────────────────────────────────────┘  │
│                                                                             │
│  ┌─────────────────────────────┐  ┌─────────────────────────────────────┐  │
│  │      Redis Cluster          │  │      Elasticsearch                   │  │
│  │                             │  │                                     │  │
│  │  • Instagram API Rate Limit │  │  • 제품 검색 인덱스                  │  │
│  │    카운터 (Sliding Window)  │  │  • 브랜드 검색                       │  │
│  │  • 세션 캐시                │  │  • 로그 분석                         │  │
│  │  • 실시간 랭킹 캐시         │  │                                     │  │
│  └─────────────────────────────┘  └─────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                        │
┌───────────────────────────────────────▼─────────────────────────────────────┐
│                           외부 서비스 연동                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────┐ ┌─────────────────────┐ ┌─────────────────────┐   │
│  │  Instagram Graph API │ │    무신사 API        │ │   Google Vision AI   │   │
│  │  (Business/Creator) │ │    (파트너스 API)     │ │   (로고 감지)        │   │
│  │                     │ │                     │ │                     │   │
│  │  • Rate Limit: 200/h │ │  • 상품 정보         │ │  • 이미지 라벨링      │   │
│  │  • Webhooks 지원     │ │  • 재고/가격         │ │  • 브랜드 로고 인식   │   │
│  └─────────────────────┘ └─────────────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Neo4j 그래프 모델 상세 설계

### 3.1 개선된 노드 구조

```cypher
// 인플루언서 노드 - 파워유저 구분을 위한 레이블 계층화
(:Influencer:Macro {id, name, instagram_id, follower_count, category, tier})
(:Influencer:Micro {id, name, instagram_id, follower_count, category, tier})
(:Influencer:Nano {id, name, instagram_id, follower_count, category, tier})

// 브랜드 노드 - 카테고리별 서브레이블
(:Brand:Fashion {id, name, category, price_range, logo_url, musinsa_id})
(:Brand:Beauty {id, name, category, price_range, logo_url})
(:Brand:Lifestyle {id, name, category, price_range, logo_url})

// 제품 노드 - 외부 플랫폼 연동
(:Product {id, name, brand_id, external_id, platform, price, image_url, category})

// 사용자 노드
(:User {id, username, email, instagram_id, created_at, last_active})

// 게시물 노드 - 시간 기반 파티셔닝 고려
(:Post {id, instagram_url, caption, like_count, comment_count, posted_at, media_type})

// 협찬 관계 중간 노드 (시간 가중치 및 이력 관리)
(:Sponsorship {id, detected_at, confidence_score, detection_method, status})
```

### 3.2 최적화된 관계 설계

```cypher
// 1. 팔로우 관계 - 최신성 가중치
(User)-[:FOLLOWS {since, interaction_score}]->(Influencer)

// 2. 게시물-인플루언서 관계
(Influencer)-[:POSTED {is_sponsored}]->(Post)

// 3. 협찬 관계 - 중간 노드 패턴 (Hyperedge 처리)
(Influencer)-[:HAS_SPONSORSHIP]->(Sponsorship)-[:FOR_BRAND]->(Brand)
(Post)-[:EVIDENCE_FOR]->(Sponsorship)

// 4. 사용자-브랜드 관심도 (실시간 업데이트)
(User)-[:INTERESTED_IN {score, updated_at, source}]->(Brand)

// 5. 제품 연결
(Post)-[:TAGS_PRODUCT {confidence}]->(Product)
(Brand)-[:OWNS]->(Product)

// 6. 사용자 행동
(User)-[:CLICKED {timestamp, source}]->(Product)
(User)-[:PURCHASED {timestamp, amount, platform}]->(Product)
(User)-[:VIEWED_POST {timestamp}]->(Post)
```

### 3.3 핵심 Cypher 쿼리 예시

```cypher
// 추천 알고리즘: 팔로우한 인플루언서의 최신 협찬 브랜드
MATCH (u:User {id: $userId})-[f:FOLLOWS]->(i:Influencer)
MATCH (i)-[:HAS_SPONSORSHIP]->(s:Sponsorship)-[:FOR_BRAND]->(b:Brand)
WHERE s.detected_at > datetime() - duration('P30D')
WITH b, 
     sum(s.confidence_score * f.interaction_score * i.follower_count) as relevance,
     collect(DISTINCT i.name) as influencers
WHERE NOT (u)-[:INTERESTED_IN]->(b)  // 이미 관심 없음 표시 제외
RETURN b, relevance, influencers
ORDER BY relevance DESC
LIMIT 20

// 실시간 트렌딩: 최근 24시간 급상승 브랜드
MATCH (i:Influencer)-[:HAS_SPONSORSHIP]->(s:Sponsorship)-[:FOR_BRAND]->(b:Brand)
WHERE s.detected_at > datetime() - duration('P1D')
WITH b, count(s) as mentions, avg(s.confidence_score) as avg_confidence
MATCH (b)<-[:FOR_BRAND]-(s2:Sponsorship)<-[:HAS_SPONSORSHIP]-(i2:Influencer)
WHERE s2.detected_at > datetime() - duration('P7D')
WITH b, mentions, avg_confidence, count(s2) as weekly_mentions
WHERE mentions > 5
RETURN b, mentions, avg_confidence, (mentions * 1.0 / weekly_mentions) as trend_score
ORDER BY trend_score DESC
```

---

## 4. Kafka 이벤트 흐름 상세

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         이벤트 처리 파이프라인                               │
└─────────────────────────────────────────────────────────────────────────────┘

[Instagram Webhook/Poller]
         │
         ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Post Collector │────▶│  Topic: post.raw │────▶│  Brand Detector │
│  (Rate Limit 관리)│     │  (JSON Payload)  │     │  (NLP + Vision) │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
                              ┌──────────────────────────┼──────────────────┐
                              │                          │                  │
                              ▼                          ▼                  ▼
                    ┌─────────────────┐        ┌─────────────────┐  ┌─────────────────┐
                    │ Topic: detection│        │  Topic: detection│  │  DLQ (실패)      │
                    │    .success     │        │     .retry       │  │                 │
                    │ (신뢰도 > 0.7)   │        │  (신뢰도 0.3~0.7)│  │  (신뢰도 < 0.3)  │
                    └────────┬────────┘        └────────┬────────┘  └─────────────────┘
                             │                          │
                             ▼                          ▼
                    ┌─────────────────┐        ┌─────────────────┐
                    │  Graph Updater  │        │  Human Review   │
                    │  (Neo4j Write)  │        │  Queue (관리자)  │
                    └────────┬────────┘        └─────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ Topic: graph.   │
                    │    updated      │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │ Recommendation  │ │   Notification  │ │   Analytics     │
    │    Engine       │ │     Service     │ │     Sink        │
    │ (사용자별 재계산) │ │  (WebSocket Push)│ │ (Elasticsearch) │
    └────────┬────────┘ └─────────────────┘ └─────────────────┘
             │
             ▼
    ┌─────────────────┐
    │ Topic: recommend│
    │    .generated   │
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │  User Service   │
    │  (캐시 업데이트)  │
    └─────────────────┘
```

---

## 5. 서비스별 상세 설계

### 5.1 인플루언서 도메인 (Influencer Domain)

```java
@Service
public class InfluencerService {

    @Value("${instagram.rate-limit:200}")
    private int hourlyLimit;

    private final RateLimiter rateLimiter;

    // Instagram Graph API Rate Limit 관리 (200/hour)
    // Sliding Window 알고리즘 사용
    public void collectPosts(List<Influencer> influencers) {
        for (Influencer influencer : influencers) {
            if (!rateLimiter.tryAcquire(influencer.getInstagramId())) {
                log.warn("Rate limit exceeded for {}", influencer.getInstagramId());
                scheduleRetry(influencer, Duration.ofMinutes(15));
                continue;
            }

            try {
                InstagramPost post = fetchFromGraphAPI(influencer);
                kafkaTemplate.send("instagram.post.raw", post);
            } catch (RateLimitException e) {
                handleRateLimitHeaders(e.getHeaders()); // X-Business-Use-Case-Usage
            }
        }
    }

    // Webhook 지원 (실시간)
    @EventListener
    public void onInstagramWebhook(InstagramWebhookEvent event) {
        // 실시간 게시물 알림 처리
        validateAndPublish(event);
    }
}
```

### 5.2 브랜드 도메인 (Brand Domain)

```java
@Component
public class BrandDetectionPipeline {

    // 멀티모달 감지 (텍스트 + 이미지)
    public DetectionResult detect(Post post) {
        // 1. 텍스트 분석 (해시태그 + 캡션)
        TextAnalysisResult textResult = nlpAnalyzer.analyze(post.getCaption());

        // 2. 이미지 분석 (Google Vision API - 옵션)
        ImageAnalysisResult imageResult = visionAnalyzer.analyze(post.getImageUrl());

        // 3. 신뢰도 계산 (앙상블)
        double confidence = calculateConfidence(textResult, imageResult);

        // 4. 브랜드 매칭
        List<BrandMatch> matches = brandRepository.findByKeywords(
            textResult.getKeywords()
        );

        return DetectionResult.builder()
            .postId(post.getId())
            .brandMatches(matches)
            .confidence(confidence)
            .method(determineMethod(textResult, imageResult))
            .build();
    }
}
```

### 5.3 추천 엔진 (Recommendation Engine)

```java
@Service
public class GraphRecommendationService {
    
    private final Neo4jClient neo4jClient;
    
    // 그래프 기반 협업 필터링
    public List<Recommendation> generateRecommendations(Long userId) {
        // Cypher 쿼리 실행
        String query = """
            MATCH (u:User {id: $userId})-[f:FOLLOWS]->(i:Influencer)
            MATCH (i)-[:HAS_SPONSORSHIP]->(s:Sponsorship)-[:FOR_BRAND]->(b:Brand)
            WHERE s.detected_at > datetime() - duration('P30D')
            AND s.confidence_score > 0.7
            WITH b, 
                 sum(s.confidence_score * f.interaction_score * 
                     log(i.follower_count)) as relevance,
                 collect(DISTINCT {name: i.name, id: i.id}) as influencers
            OPTIONAL MATCH (b)-[:OWNS]->(p:Product)
            WHERE p.platform = 'MUSINSA'
            WITH b, relevance, influencers, 
                 collect(p)[0..3] as products
            ORDER BY relevance DESC
            LIMIT 20
            RETURN b, relevance, influencers, products
            """;
            
        return neo4jClient.query(query)
            .bind(userId).to("userId")
            .fetchAs(Recommendation.class)
            .mappedBy(this::mapToRecommendation)
            .all();
    }
}
```

---

## 6. 데이터 흐름 및 처리량 설계

| 구간 | 처리량 | 지연 시간 | 처리 방식 |
|------|--------|-----------|-----------|
| Instagram 수집 | 200 req/hour/API Key | 폴링 15분 | 제한적 병렬 |
| 브랜드 감지 | 1000 posts/min | < 2초 | Kafka Streams 병렬 처리 |
| 그래프 업데이트 | 5000 writes/sec | < 50ms | Neo4j Bolt 프로토콜 |
| 추천 생성 | 사용자당 100ms | < 500ms | Read Replica 쿼리 |
| 실시간 알림 | 10,000 concurrent | < 100ms | WebSocket + Redis Pub/Sub |

---

## 7. 확장성 및 장애 대응

### 7.1 샤딩 전략
- **Neo4j**: 인플루언서 ID 기반 샤딩 (Fabric 기능 활용)
- **PostgreSQL**: 사용자 ID 기반 파티셔닝
- **Kafka**: Topic 파티셔닝 (influencer_id 기분)

### 7.2 장애 시나리오
| 장애 | 대응 |
|------|------|
| Instagram API 중단 | 백업 크롤러 활성화 (Headless Chrome) + 캐시 데이터 활용 |
| Neo4j Write 지연 | Kafka Lag 모니터링, Consumer Scale-out |
| 브랜드 감지 실패 | 신뢰도 낮은 결과는 수동 검수 큐로 이동 |
| 외부 API 장애 | Circuit Breaker (Resilience4j) + Fallback 캐시 |

---

## 8. 구현 로드맵 (수정)

### Phase 1: 인프라 및 코어 (3주)
- [ ] Neo4j 5.x 클러스터 구축 (Core + Read Replica)
- [ ] Kafka 클러스터 구축 (3 brokers)
- [ ] Spring Boot 3.2 + Java 21 마이그레이션 (Virtual Threads 활용)
- [ ] 도메인 기반 아키텍처 구축 (DDD)

### Phase 2: 인플루언서 도메인 구현 (2주)
- [ ] 인플루언서 관리 기능
- [ ] Instagram Graph API 연동
- [ ] Rate Limit 관리 시스템 (Redis 기반)
- [ ] 게시물 수집 및 Webhook 처리

### Phase 3: 브랜드/추천 도메인 구현 (3주)
- [ ] 브랜드 감지 NLP 모델 (KoBERT 기반 한국어 최적화)
- [ ] 브랜드 감지 파이프라인 구현
- [ ] Neo4j GDS (Graph Data Science) 통합
- [ ] 실시간 추천 엔진

### Phase 4: 상용화 (2주)
- [ ] 무신사 API 연동 (파트너스 프로그램 가입 필요)
- [ ] 캐시 전략 최적화 (Redis + Caffeine)
- [ ] 모니터링 및 알람 (Prometheus + Grafana)
- [ ] 부하 테스트 (K6)

---

## 9. 도메인 구조

### 최종 도메인 구조 (7개 도메인)

```
com.example.fashionithm/
├── common/                 # 공통 유틸리티 및 설정
├── brand/                  # 브랜드 관리 + 브랜드 감지 기능
├── influencer/             # 인플루언서 관리 + 데이터 수집 + 게시물 기능
├── notification/           # 알림 서비스
├── product/                # 제품 관리
├── recommendation/         # 추천 엔진
├── sponsorship/            # 협찬 관계
└── user/                   # 사용자 관리 + 인증 기능
```

### 도메인 통합 현황

| 통합 전 | 통합 후 | 이유 |
|---------|---------|------|
| authentication | user | 인증 기능은 사용자 도메인의 일부 |
| datacollection | influencer | 데이터 수집은 인플루언서 도메인의 인프라 |
| detection | brand | 브랜드 감지는 브랜드 도메인의 핵심 기능 |
| external | 각 도메인의 infrastructure | 외부 연동은 관련 도메인에 분산 |
| post | influencer | 게시물은 인플루언서의 하위 개념 |
