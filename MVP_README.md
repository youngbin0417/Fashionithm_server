# Instagram Monitoring MVP

인스타그램 모니터링 MVP - 데이터베이스 없이 하드코딩된 데이터로 동작

## 실행 방법

```bash
# 빌드
gradlew clean build -x test

# 실행
java -jar build/libs/fashionithm-0.0.1-SNAPSHOT.jar
```

## API 엔드포인트

### 인플루언서
- `GET /api/influencers` - 모든 인플루언서 조회
- `GET /api/influencers/{id}` - 인플루언서 ID 로 조회
- `GET /api/influencers/category/{category}` - 카테고리별 조회
- `GET /api/influencers/instagram/{instagramId}` - 인스타그램 ID 로 조회
- `POST /api/influencers/{instagramId}/monitor` - 인플루언서 모니터링
- `POST /api/influencers/monitor-all` - 전체 모니터링

### 게시물
- `GET /api/posts` - 모든 게시물 조회
- `GET /api/posts/{id}` - 게시물 ID 로 조회
- `GET /api/posts/influencer/{instagramId}` - 인플루언서별 게시물
- `GET /api/posts/recent/{days}` - 최근 게시물
- `GET /api/posts/{id}/detect` - 게시물 브랜드 감지

### 브랜드
- `GET /api/brands` - 모든 브랜드 조회
- `GET /api/brands/{id}` - 브랜드 ID 로 조회
- `GET /api/brands/category/{category}` - 카테고리별 조회
- `GET /api/brands/{name}/mentions` - 브랜드 언급 게시물
- `GET /api/brands/search/{keyword}` - 키워드 검색

## 샘플 응답

### 전체 모니터링
```bash
curl -X POST http://localhost:8080/api/influencers/monitor-all
```

응답 예시:
- 총 7 개 게시물 모니터링
- 11 개 브랜드 언급 감지
- Musinsa(2), Zara(2), Nike(1), Adidas(1), H&M(1), Uniqlo(1), Ably(1), COS(1), Starbucks(1)

### 브랜드 감지 로직
- 해시태그 기반 키워드 매칭
- 신뢰도 점수 계산 (0.5 ~ 1.0)
  - 기본 점수: 0.5
  - 해시태그 언급: +0.2
  - 공식 키워드 매칭: +0.15
  - 중복 언급: 최대 +0.15

## 하드코딩 데이터

### 인플루언서 (5 명)
- Fashion Kim (150K followers)
- Style Index (85K followers)
- Daily Look (45K followers)
- Street Fashion (120K followers)
- Minimal Look (67K followers)

### 게시물 (7 개)
- 각 인플루언서의 최근 게시물
- 브랜드 해시태그 포함

### 브랜드 (9 개)
- Musinsa, Zara, Ably, Uniqlo, H&M
- Nike, Adidas, COS, Starbucks
