# 📚 KT 걷다가서재 - AI 기반 자동 출간 및 구독 플랫폼

> 클라우드 네이티브 기반 전자책 자동 출간 및 구독 플랫폼  
> Spring Boot + React + Azure + Kafka 기반 마이크로서비스 프로젝트

---

## 🛠️ 프로젝트 개요

- 작가가 글을 작성하면 AI가 자동으로 표지 이미지를 생성하고 전자책으로 출간
- 구독자는 월정액으로 책을 열람하고, KT 고객은 포인트 혜택을 받을 수 있음
- 3회 이상 열람된 도서는 '베스트셀러'로 분류되어 추가 노출
- 실습 목표: 클라우드 네이티브 앱 개발 및 MSA 아키텍처 경험

---

## 📆 개발 기간

- **2025.06.25 (수) ~ 2025.07.04 (금)** / 총 8일

---

## 👥 팀원 (AI_04반_11조)

| 이름   | 역할             |담당 파트
|--------|------------------|------------------|
| 이헌준 | 조장              | 전체 관리 및 PM |
| 김시훈 | 발표자 | AI 출간 자동화 담당 |
| 안도형 | PPT | 구독자 관리 |
| 박수연 | PPT | 서재 플랫폼 |
| 오현종 | 검토 | 집필 관리  |
| 양성현 | 검토 | 포인트 관리 |
| 김민서 | 검토 | 작가 관리  |
| 류근우 | 서기              | 전체 관리 및 연결 진행 |

---

## 🗂️ 주요 일정 및 작업

- **1일차 (06.25)**: OT, 도메인 이해, 역할 분담, User Story, Event Storming
- **2일차 (06.26)**: 전략/전술 설계, 헥사고날 다이어그램
- **3일차 (06.27)**: 마이크로서비스 구현 시작 (Event 기반)
- **4일차 (06.30)**: Kafka 메시징, CQRS, 테스트 작성
- **5일차 (07.01)**: 프론트엔드 구현, Docker 이미지 생성 및 Push
- **6일차 (07.02)**: Azure 배포, 오케스트레이션(HPA, 무정지 배포)
- **7일차 (07.03)**: 서비스 메시 적용, 모니터링 & 로깅
- **8일차 (07.04)**: 배포 파이프라인 설계, Wrap-up, 발표

---
# 소스 코드 추가 설명

## AI Service

### 1. AI 요약 생성 (ContentAnalyzer)
- **트리거**: `BookRegisted` 이벤트 수신
- **처리**: GPT를 활용한 콘텐츠 요약 + 장르 분류
- **발행**: `AiSummarized` 이벤트
- **핵심 로직**: 중복 처리 방지, 2단계 AI 처리 (요약 → 분류)

```java
// AI 요약 생성 - 2단계 처리
String initialSummary = gptService.generateSummary(
    bookRegisted.getContext(), 500, "KO", "일반요약"
);
String classifiedGenre = gptService.classifyGenre(
    bookRegisted.getTitle(), initialSummary
);

// 이벤트 발행
AiSummarized aiSummarized = new AiSummarized(contentAnalyzer);
aiSummarized.publishAfterCommit();
```

### 2. AI 표지 생성 (CoverDesign)
- **트리거**: REST API 요청 또는 `AiSummarized` 이벤트
- **처리**: DALL-E를 활용한 표지 이미지 생성
- **발행**: `CoverCreated` 이벤트
- **핵심 로직**: 요약 결과 활용으로 고품질 표지 생성

```java
// AI 표지 생성
DalleService dalleService = new DalleService();
String imageUrl = dalleService.generateCoverImage(title, context);

// 요약 결과 활용한 고품질 표지 생성
String contextForImage = analyzer.getSummary() != null ? 
    analyzer.getSummary() : analyzer.getContext();

// 이벤트 발행
CoverCreated coverCreated = new CoverCreated(coverDesign);
coverCreated.publishAfterCommit();
```

### 3. 이벤트 오케스트레이션 (PolicyHandler)
- **트리거**: Kafka 이벤트 스트림 수신
- **처리**: 이벤트 기반 순차 처리 및 중복 방지
- **발행**: 없음 (다른 Aggregate의 이벤트 발행을 트리거)
- **핵심 로직**: 중복 처리 방지, 순차 실행, 품질 개선 처리

```java
// BookRegisted 이벤트 처리 - 순차적 AI 처리
@StreamListener(condition = "headers['type']=='BookRegisted'")
public void wheneverBookRegisted_ProcessSequentially(@Payload BookRegisted bookRegisted) {
    // 중복 처리 방지
    boolean contentAnalysisExists = !existingAnalyzers.isEmpty() && 
        existingAnalyzers.get(0).getSummary() != null;
    boolean coverExists = existingCover.isPresent() && 
        existingCover.get().getImageUrl() != null;
    
    if (contentAnalysisExists && coverExists) {
        return; // 중복 처리 방지
    }
    
    // 순차적 AI 처리
    ContentAnalyzer.aiSummarize(event);
    CoverDesign.autoCoverGeneratePolicy(event);
}

// AiSummarized 이벤트 처리 - 품질 개선
@StreamListener(condition = "headers['type']=='AiSummarized'")
public void wheneverAiSummarized_GenerateCoverWithSummary(@Payload AiSummarized aiSummarized) {
    // 요약 결과를 활용한 고품질 표지 생성
    CoverDesign.generateCoverWithSummary(event);
}
```

## AuthorManage Service

### 1. 작가 승인 처리 (Author Aggregate)
- **트리거**: 관리자의 `등록 승인` 커맨드
- **처리**: 작가 승인 상태 업데이트
- **발행**: `AuthorApproved` 이벤트
- **핵심 로직**: 관리자 승인 워크플로우, 작가 상태 관리

```java
// 작가 승인 이벤트 발행
AuthorApproved authorApproved = new AuthorApproved(author);
authorApproved.setAuthorId(author.getAuthorId());
authorApproved.setIsApproved(true);
authorApproved.setName(author.getName());
// 이벤트 발행으로 다른 서비스에 작가 승인 상태 전파
```

### AuthorApproved 이벤트 구조
```java
@Data
@ToString
public class AuthorApproved extends AbstractEvent {
    private Long authorId;      // 작가 ID
    private Boolean isApproved; // 승인 상태
    private String name;        // 작가 이름
}
```

## WriteManage Service 

### 1. 책 등록 처리 (Writing Aggregate)
- **트리거**: 작가의 `책 등록` 커맨드
- **처리**: 책 정보 저장 및 등록 상태 설정
- **발행**: `BookRegisted` 이벤트
- **핵심 로직**: 작가 인증 후 책 등록, 글 작성/수정/삭제 관리

```java
// 책 등록 이벤트 발행
BookRegisted bookRegisted = new BookRegisted(writing);
bookRegisted.setBookId(writing.getBookId());
bookRegisted.setContext(writing.getContext());
bookRegisted.setAuthorId(writing.getAuthorId());
bookRegisted.setTitle(writing.getTitle());
bookRegisted.setRegistration(true);
// 이벤트 발행으로 AI 서비스 등 다른 서비스에 책 등록 정보 전파
```

### BookRegisted 이벤트 구조
```java
@Data
@ToString
public class BookRegisted extends AbstractEvent {
    private Long bookId;        // 책 ID
    private String context;     // 책 내용
    private Long authorId;      // 작가 ID
    private String title;       // 책 제목
    private Boolean registration; // 등록 상태
}
```

## SubscribeManage Service 

### 1. 사용자 등록 처리 (User Aggregate)
- **트리거**: 사용자의 `회원가입` 커맨드
- **처리**: 사용자 정보 저장 및 등록 상태 설정
- **발행**: `UserRegistered` 이벤트
- **핵심 로직**: 사용자 계정 생성, 구독 관리 준비

```java
// 사용자 등록 이벤트 발행
UserRegistered userRegistered = new UserRegistered(user);
userRegistered.setUserId(user.getUserId());
userRegistered.setIsKt(user.getIsKt());
// 이벤트 발행으로 다른 서비스에 사용자 등록 정보 전파
```

### UserRegistered 이벤트 구조
```java
@Data
@ToString
public class UserRegistered extends AbstractEvent {
    private Long userId;    // 사용자 ID
    private String isKt;    // KT 계정 여부
}
```

### 2. 사용자 정보 수정 처리 (User Aggregate)
- **트리거**: 사용자의 `회원정보수정` 커맨드
- **처리**: 사용자 정보 업데이트
- **발행**: `UserUpdated` 이벤트
- **핵심 로직**: 사용자 정보 변경 이력 관리

```java
// 사용자 정보 수정 이벤트 발행
UserUpdated userUpdated = new UserUpdated(user);
userUpdated.setUserId(user.getUserId());
userUpdated.setIsKt(user.getIsKt());
// 이벤트 발행으로 다른 서비스에 사용자 정보 변경 전파
```

### 3. 구독 서비스 가입 처리 (User Aggregate)
- **트리거**: 사용자의 `구독가입` 커맨드
- **처리**: 월 구독 상태 활성화
- **발행**: `BookServiceSubscribed` 이벤트
- **핵심 로직**: 구독 상태 관리, 월 구독 활성화

```java
// 구독 서비스 가입 이벤트 발행
BookServiceSubscribed bookServiceSubscribed = new BookServiceSubscribed(user);
bookServiceSubscribed.setUserId(user.getUserId());
bookServiceSubscribed.setIsSubscribe("true");
// 이벤트 발행으로 Library 서비스에 구독 상태 전파
```

### 4. 개별 책 구매 처리 (User Aggregate)
- **트리거**: 사용자의 `소장` 커맨드
- **처리**: 포인트 차감 및 책 소장 권한 부여
- **발행**: `BuyBookSub` 이벤트
- **핵심 로직**: 포인트 결제, 개별 책 소장 관리

```java
// 책 구매 이벤트 발행
BuyBookSub buyBookSub = new BuyBookSub(user);
buyBookSub.setUserId(user.getUserId());
buyBookSub.setBookId(targetBookId);
// 이벤트 발행으로 Library 서비스에 소장 정보 전파
```

### 주요 이벤트 구조
```java
// 사용자 정보 수정
@Data
public class UserUpdated extends AbstractEvent {
    private Long userId;
    private String isKt;
}

// 구독 서비스 가입
@Data
public class BookServiceSubscribed extends AbstractEvent {
    private Long userId;
    private String isSubscribe; // "true" or "false"
}

// 개별 책 구매
@Data
public class BuyBookSub extends AbstractEvent {
    private Long userId;
    private Long bookId;
}
```
### 5. 이벤트 처리 정책 (PolicyHandler)
- **트리거**: 외부 서비스 이벤트 수신
- **처리**: 포인트 동기화 및 도서 정보 동기화
- **핵심 로직**: 포인트 서비스와 도서 서비스 간 데이터 일관성 유지

```java
// 포인트 감소 이벤트 처리
@StreamListener(condition = "headers['type']=='PointDecreased'")
public void wheneverPointDecreased_PointSyncPolicy(@Payload PointDecreased pointDecreased) {
    User.pointSyncPolicy(event); // 사용자 포인트 잔액 동기화
}

// 포인트 충전 이벤트 처리
@StreamListener(condition = "headers['type']=='PointRecharged'")
public void wheneverPointRecharged_PointSyncPolicy(@Payload PointRecharged pointRecharged) {
    User.pointSyncPolicy(event); // 사용자 포인트 잔액 동기화
}

// KT 가입 포인트 지급 이벤트 처리
@StreamListener(condition = "headers['type']=='KtSignedupPointCharged'")
public void wheneverKtSignedupPointCharged_PointSyncPolicy(@Payload KtSignedupPointCharged ktSignedupPointCharged) {
    User.pointSyncPolicy(event); // KT 가입 혜택 포인트 동기화
}

// 일반 가입 포인트 지급 이벤트 처리
@StreamListener(condition = "headers['type']=='StandardSignedupPointCharged'")
public void wheneverStandardSignedupPointCharged_PointSyncPolicy(@Payload StandardSignedupPointCharged standardSignedupPointCharged) {
    User.pointSyncPolicy(event); // 일반 가입 혜택 포인트 동기화
}

// 도서 출간 이벤트 처리
@StreamListener(condition = "headers['type']=='Published'")
public void wheneverPublished_BookInfoPolicy(@Payload Published published) {
    Library.bookInfoPolicy(event); // 도서 정보 Library에 동기화
}
```

### 처리하는 외부 이벤트
- **PointDecreased**: 포인트 차감 (구매 시)
- **PointRecharged**: 포인트 충전
- **KtSignedupPointCharged**: KT 가입 혜택 포인트 지급
- **StandardSignedupPointCharged**: 일반 가입 혜택 포인트 지급
- **Published**: 도서 출간 정보


## Point Service 

### 1. 포인트 차감 처리 (개별 책 구매)
- **트리거**: `BuyBookSub` 이벤트 수신
- **처리**: 책 구매 시 포인트 차감
- **발행**: `PointDecreased` 이벤트
- **핵심 로직**: 사용자 포인트 잔액 확인 및 차감

```java
@StreamListener(condition = "headers['type']=='BuyBookSub'")
public void wheneverBuyBookSub_PointDecrease(@Payload BuyBookSub buyBookSub) {
    Point.pointDecrease(event); // 개별 책 구매 시 포인트 차감
}
```

### 2. 포인트 차감 처리 (구독 서비스)
- **트리거**: `BookServiceSubscribed` 이벤트 수신
- **처리**: 월 구독 서비스 가입 시 포인트 차감
- **발행**: `PointDecreased` 이벤트
- **핵심 로직**: 구독료 포인트 결제 처리

```java
@StreamListener(condition = "headers['type']=='BookServiceSubscribed'")
public void wheneverBookServiceSubscribed_PointDecrease(@Payload BookServiceSubscribed bookServiceSubscribed) {
    Point.pointDecrease(event); // 구독 서비스 가입 시 포인트 차감
}
```

### 3. 초기 포인트 지급 처리
- **트리거**: `UserRegistered` 이벤트 수신
- **처리**: 신규 회원 가입 시 초기 포인트 지급
- **발행**: `StandardSignedupPointCharged` 이벤트
- **핵심 로직**: 신규 회원 웰컴 포인트 지급

```java
@StreamListener(condition = "headers['type']=='UserRegistered'")
public void wheneverUserRegistered_InitialPointPolicy(@Payload UserRegistered userRegistered) {
    Point.initialPointPolicy(event); // 신규 회원 초기 포인트 지급
}
```

### 4. KT 가입 혜택 포인트 처리
- **트리거**: `UserUpdated` 이벤트 수신
- **처리**: KT 계정 연동 시 추가 포인트 지급
- **발행**: `KtSignedupPointCharged` 이벤트
- **핵심 로직**: KT 제휴 혜택 포인트 지급

```java
@StreamListener(condition = "headers['type']=='UserUpdated'")
public void wheneverUserUpdated_KtSignedupPointPolicy(@Payload UserUpdated userUpdated) {
    Point.ktSignedupPointPolicy(event); // KT 계정 연동 시 추가 포인트 지급
}
```

### 5. 포인트 충전 처리
- **트리거**: 사용자의 `포인트충전` 커맨드
- **처리**: 사용자 포인트 잔액 증가
- **발행**: `PointRecharged` 이벤트
- **핵심 로직**: 포인트 충전 및 잔액 업데이트

### 주요 발행 이벤트
- **PointDecreased**: 포인트 차감 완료
- **PointRecharged**: 포인트 충전 완료
- **KtSignedupPointCharged**: KT 가입 혜택 포인트 지급
- **StandardSignedupPointCharged**: 일반 가입 혜택 포인트 지급

### 이벤트 구조
```java
// 포인트 차감 완료 이벤트
@Data
@ToString
public class PointDecreased extends AbstractEvent {
    private Long userId;        // 사용자 ID
    private Integer pointBalance; // 차감 후 잔액
}

// 포인트 충전 완료 이벤트
@Data
@ToString
public class PointRecharged extends AbstractEvent {
    private Long userId;        // 사용자 ID
    private Integer pointBalance; // 충전 후 잔액
}

// KT 가입 혜택 포인트 지급 이벤트
@Data
@ToString
public class KtSignedupPointCharged extends AbstractEvent {
    private Long userId;        // 사용자 ID
    private Integer pointBalance; // 지급 후 잔액
}

// 일반 가입 혜택 포인트 지급 이벤트
@Data
@ToString
public class StandardSignedupPointCharged extends AbstractEvent {
    private Long userId;        // 사용자 ID
    private Integer pointBalance; // 지급 후 잔액
}
```

## Point Service - PolicyHandler

### 이벤트 처리 정책
```java
@Service
@Transactional
public class PolicyHandler {
    @Autowired
    PointRepository pointRepository;

    // 개별 책 구매 시 포인트 차감
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='BuyBookSub'")
    public void wheneverBuyBookSub_PointDecrease(@Payload BuyBookSub buyBookSub) {
        BuyBookSub event = buyBookSub;
        System.out.println("\n\n##### listener PointDecrease : " + buyBookSub + "\n\n");
        Point.pointDecrease(event);
    }

    // 구독 서비스 가입 시 포인트 차감
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='BookServiceSubscribed'")
    public void wheneverBookServiceSubscribed_PointDecrease(@Payload BookServiceSubscribed bookServiceSubscribed) {
        BookServiceSubscribed event = bookServiceSubscribed;
        System.out.println("\n\n##### listener PointDecrease : " + bookServiceSubscribed + "\n\n");
        Point.pointDecrease(event);
    }

    // 신규 회원 가입 시 초기 포인트 지급
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='UserRegistered'")
    public void wheneverUserRegistered_InitialPointPolicy(@Payload UserRegistered userRegistered) {
        UserRegistered event = userRegistered;
        System.out.println("\n\n##### listener InitialPointPolicy : " + userRegistered + "\n\n");
        Point.initialPointPolicy(event);
    }

    // KT 계정 연동 시 추가 포인트 지급
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='UserUpdated'")
    public void wheneverUserUpdated_KtSignedupPointPolicy(@Payload UserUpdated userUpdated) {
        UserUpdated event = userUpdated;
        System.out.println("\n\n##### listener KtSignedupPointPolicy : " + userUpdated + "\n\n");
        Point.ktSignedupPointPolicy(event);
    }
}
```

### 핵심 이벤트 처리 로직
- **BuyBookSub 이벤트**: 개별 책 구매 시 포인트 차감 처리
- **BookServiceSubscribed 이벤트**: 월 구독 서비스 가입 시 포인트 차감 처리
- **UserRegistered 이벤트**: 신규 회원 가입 시 초기 포인트 지급 정책 실행
- **UserUpdated 이벤트**: KT 계정 연동 시 추가 포인트 지급 정책 실행



## LibraryPlatform Service

### 발행하는 이벤트 구조
```java
// 도서 출간 완료 이벤트
@Data
@ToString
public class Published extends AbstractEvent {
    private Long bookId;             // 도서 ID
    private Long authorId;           // 작가 ID
    private String authorName;       // 작가명
    private String title;            // 도서 제목
    private String imageUrl;         // AI 생성 표지 URL
    private String summary;          // AI 생성 요약
    private String context;          // 도서 내용
    private String classificationType; // AI 분류 장르
    private Date publishDate;        // 출간일
    private Long selectCount;        // 선택(구매) 횟수
    private Integer rank;            // 랭킹
    private Boolean bestseller;      // 베스트셀러 여부
}

// 베스트셀러 선정 이벤트
@Data
@ToString
public class BestsellerGiven extends AbstractEvent {
    private Long bookId;             // 도서 ID
    private Long authorId;           // 작가 ID
    private String authorName;       // 작가명
    private String title;            // 도서 제목
    private String imageUrl;         // AI 생성 표지 URL
    private String summary;          // AI 생성 요약
    private String context;          // 도서 내용
    private String classificationType; // AI 분류 장르
    private Date publishDate;        // 출간일
    private Long selectCount;        // 선택(구매) 횟수
    private Integer rank;            // 랭킹
    private Boolean bestseller;      // 베스트셀러 여부 (true)
}
```

### 비즈니스 가치
- **완성도 보장**: AI 요약과 표지가 모두 완료된 도서만 출간
- **이벤트 동기화**: 비동기 이벤트들의 조합을 통한 완전한 도서 정보 생성
- **사용자 라이브러리**: 구매한 도서의 실시간 라이브러리 반영
- **품질 관리**: 모든 AI 처리가 완료된 후 도서 출간 처리
- **랭킹 시스템**: 구매 횟수 기반 베스트셀러 선정 및 랭킹 관리
- **통합 도서 정보**: AI 생성 콘텐츠와 메타데이터를 통합한 완전한 도서 정보 제공

## LibraryPlatform Service - PolicyHandler

### 이벤트 처리 정책
```java
@Service
@Transactional
public class PolicyHandler {
    @Autowired
    LibraryInfoRepository libraryInfoRepository;

    // 임시 저장소: bookId 기준으로 이벤트 매칭
    private Map<Long, AiSummarized> aiSummarizedMap = new ConcurrentHashMap<>();
    private Map<Long, CoverCreated> coverCreatedMap = new ConcurrentHashMap<>();

    // 개별 책 구매 시 소장중인 도서 정보 업데이트
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='BuyBookSub'")
    public void wheneverBuyBookSub_BuyBookIncrease(@Payload BuyBookSub buyBookSub) {
        System.out.println("\n\n##### listener BuyBookIncrease : " + buyBookSub + "\n\n");
        LibraryInfo.buyBookIncrease(buyBookSub);
    }

    // AI 요약 완료 이벤트 수신 및 임시 저장
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='AiSummarized'")
    public void wheneverAiSummarized(@Payload AiSummarized aiSummarized) {
        Long bookId = aiSummarized.getBookId();
        aiSummarizedMap.put(bookId, aiSummarized);
        System.out.println("\n\n##### listener Received AiSummarized : " + aiSummarized + "\n\n");
        publishIfReady(bookId);
    }

    // AI 표지 생성 완료 이벤트 수신 및 임시 저장
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='CoverCreated'")
    public void wheneverCoverCreated(@Payload CoverCreated coverCreated) {
        Long bookId = coverCreated.getBookId();
        coverCreatedMap.put(bookId, coverCreated);
        System.out.println("\n\n##### listener Received CoverCreated : " + coverCreated + "\n\n");
        publishIfReady(bookId);
    }

    // 두 이벤트가 모두 수신되면 출간된 도서 정보 발행
    private void publishIfReady(Long bookId) {
        AiSummarized aiEvent = aiSummarizedMap.get(bookId);
        CoverCreated coverEvent = coverCreatedMap.get(bookId);

        if (aiEvent != null && coverEvent != null) {
            System.out.println("\n\n##### Publishing LibraryInfo for bookId: " + bookId + "\n\n");
            LibraryInfo.publish(aiEvent, coverEvent); // 출간된 도서 정보 발행
            aiSummarizedMap.remove(bookId);
            coverCreatedMap.remove(bookId);
        }
    }
}
```

### 핵심 이벤트 처리 로직
- **BuyBookSub 이벤트**: 개별 책 구매 시 사용자 라이브러리에 도서 추가
- **AiSummarized 이벤트**: AI 요약 완료 시 임시 저장 후 대기
- **CoverCreated 이벤트**: AI 표지 생성 완료 시 임시 저장 후 대기
- **조건부 발행**: 요약과 표지가 모두 완료되면 `출간된` 도서 정보 발행

### 이벤트 조합 처리 (Event Orchestration)
```java
// 이벤트 매칭 전략
private Map<Long, AiSummarized> aiSummarizedMap = new ConcurrentHashMap<>();
private Map<Long, CoverCreated> coverCreatedMap = new ConcurrentHashMap<>();

// 두 이벤트가 모두 도착하면 처리
if (aiEvent != null && coverEvent != null) {
    LibraryInfo.publish(aiEvent, coverEvent); // 출간된 도서 정보 발행
    // 임시 저장소에서 제거
    aiSummarizedMap.remove(bookId);
    coverCreatedMap.remove(bookId);
}
```

### 발행하는 이벤트
- **출간된**: AI 요약 + 표지 생성 완료된 도서 정보
- **베스트셀러랭킹추어업**: 도서 판매량 기반 랭킹 업데이트


<br><br>


