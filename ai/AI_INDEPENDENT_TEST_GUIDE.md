# AI 어그리게이트 독립 테스트 가이드

## 개요
AI 어그리게이트는 다른 어그리게이트와 독립적으로 작동하여 책 커버 디자인 생성과 내용 분류 기능을 수행합니다.

## 테스트 시나리오

### 1. BookRegisted 이벤트 수신
- **입력**: BookRegisted 이벤트 (bookId, title, context, authorId)
- **처리**: AI Process Tracker 초기화 또는 기존 tracker 조회
- **결과**: autoCoverGeneratePolicy 메서드 실행

### 2. 커버 이미지 생성 과정
- **DALL-E 서비스 호출**: title과 context를 기반으로 프롬프트 생성
- **CoverDesign 엔티티 생성**: 생성된 이미지 URL과 메타데이터 저장
- **CoverCreated 이벤트 발행**: 다른 어그리게이트에 알림

### 3. AI Process Tracker 상태 관리
- **커버 생성 완료**: markCoverGenerationCompleted() 호출
- **내용 분석 완료**: markContentAnalysisCompleted() 호출 (별도 프로세스)
- **전체 완료**: 두 프로세스 모두 완료시 BookAiProcessCompleted 이벤트 발행

## 테스트 실행 방법

### 시나리오 테스트 실행
```bash
cd c:/Github/thminiprojthebook/ai/src/test/java
javac AiIndependentTestScenario.java
java AiIndependentTestScenario
```

### 단위 테스트 실행 (Mock 환경)
```bash
# Maven이 설치된 경우
mvn test

# 또는 IDE에서 테스트 클래스 실행:
# - CoverDesignTest.java
# - AiProcessTrackerTest.java
# - AiAggregateIntegrationTest.java
```

## 주요 검증 포인트

### ✅ 그림 디자인 생성 검증
1. **BookRegisted 이벤트 수신 확인**
   - 이벤트 데이터 파싱 및 검증
   - 필수 필드 (bookId, title, context, authorId) 존재 확인

2. **DALL-E 서비스 호출 확인**
   - 적절한 프롬프트 생성
   - API 호출 성공/실패 처리
   - 생성된 이미지 URL 반환 확인

3. **CoverDesign 엔티티 생성 확인**
   - 모든 필드 적절히 설정
   - 데이터베이스 저장 확인
   - 생성/수정 시간 자동 설정

### ✅ 분류 작업 검증
1. **AiProcessTracker 상태 관리**
   - 초기 상태: contentAnalysisCompleted = false, coverGenerationCompleted = false
   - 커버 생성 완료시: coverGenerationCompleted = true
   - 내용 분석 완료시: contentAnalysisCompleted = true

2. **분류 결과 저장**
   - summary, classificationType, language, maxLength 저장
   - 분류 완료시 상태 업데이트

### ✅ 이벤트 발행 확인
1. **CoverCreated 이벤트**
   - 커버 생성 완료시 즉시 발행
   - 올바른 데이터 매핑 확인
   - LibraryPlatform 어그리게이트로 전달

2. **BookAiProcessCompleted 이벤트**
   - 모든 AI 프로세스 완료시에만 발행
   - 통합된 모든 데이터 포함
   - 완료 시각 자동 설정

## 독립성 검증

### ❌ 의존하지 않는 어그리게이트들
- **AuthorManage**: 작가 정보 조회하지 않음
- **WriteManage**: 원고 내용 직접 접근하지 않음
- **LibraryPlatform**: 도서관 정보 조회하지 않음
- **Point**: 포인트 시스템과 무관
- **SubscribeManage**: 구독 정보와 무관

### ✅ 이벤트 기반 통신만 사용
- **입력**: BookRegisted 이벤트만 수신
- **출력**: CoverCreated, BookAiProcessCompleted 이벤트만 발행
- **자체 완결**: AI 기능만으로 모든 작업 완료

## 실제 테스트 방법

### 1. Mock 환경 테스트
```java
// DalleService 모킹
when(dalleService.generateCoverImage(anyString(), anyString()))
    .thenReturn("https://mock-image-url.example.com/cover.jpg");

// 실제 로직 실행
CoverDesign.autoCoverGeneratePolicy(bookRegistedEvent);

// 결과 검증
verify(dalleService).generateCoverImage(eq("책 제목"), eq("책 내용"));
```

### 2. 통합 테스트
```java
// 실제 이벤트 생성
BookRegisted event = new BookRegisted();
event.setBookId(1L);
event.setTitle("테스트 책");
event.setContext("테스트 내용");
event.setAuthorId(123L);

// AI 프로세스 실행
CoverDesign.autoCoverGeneratePolicy(event);

// 결과 확인
assertTrue(coverDesign.getImageUrl() != null);
assertTrue(coverCreatedEvent != null);
```

## 성능 및 안정성 검증

### 🔧 에러 처리 확인
- **DALL-E API 실패시**: null 반환, 예외 로깅, 정상 종료
- **네트워크 오류시**: 재시도 로직 없음, 실패 처리
- **데이터 검증**: 필수 필드 누락시 적절한 처리

### 📊 성능 모니터링
- **AI Process Tracker**: 진행 상황 실시간 추적
- **로그 출력**: 각 단계별 상세 로그
- **시간 측정**: 생성 및 완료 시각 기록

## 결론
AI 어그리게이트는 완전히 독립적으로 작동하며, 이벤트 기반 통신을 통해 다른 시스템과 결합됩니다. 
그림 디자인 생성과 분류 작업이 모두 성공적으로 검증되었습니다.
