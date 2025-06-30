# 실제 DALL-E API를 사용한 이미지 생성 가이드

이 가이드는 mock 서비스가 아닌 실제 OpenAI DALL-E API를 사용하여 책 표지 이미지를 생성하는 방법을 설명합니다.

## 🔑 OpenAI API 키 설정

### 1. OpenAI API 키 획득
1. https://openai.com/api/ 에서 계정을 생성하세요
2. API 키를 생성하세요
3. 결제 정보를 등록하세요 (API 사용 시 비용 발생)

### 2. API 키 설정 방법

#### 옵션 A: 환경변수 설정 (권장)
```bash
# Windows (PowerShell)
$env:OPENAI_API_KEY="your-actual-api-key-here"

# Windows (CMD)
set OPENAI_API_KEY=your-actual-api-key-here

# Linux/Mac
export OPENAI_API_KEY="your-actual-api-key-here"
```

#### 옵션 B: application.yml 직접 수정
```yaml
spring:
  ai:
    openai:
      api-key: your-actual-api-key-here  # 실제 API 키로 교체
```

⚠️ **주의**: application.yml에 직접 API 키를 적는 것은 보안상 권장하지 않습니다.

## 🚀 실제 이미지 생성 테스트 실행

### 1. 컴파일 및 실행
```bash
cd c:\Github\thminiprojthebook\ai

# 컴파일
mvn compile

# 테스트 실행 (Java 클래스 직접 실행)
mvn exec:java -Dexec.mainClass="thminiprojthebook.integration.RealImageGenerationTest"
```

### 2. 터미널에서 직접 실행
```bash
# 클래스 경로 설정하여 실행
java -cp target/classes:target/test-classes thminiprojthebook.integration.RealImageGenerationTest
```

## 📊 결과 확인

### 1. 콘솔 출력
- 실시간으로 이미지 생성 진행 상황을 볼 수 있습니다
- 성공 시 실제 이미지 URL이 출력됩니다

### 2. 결과 파일
생성된 결과는 다음 위치에 저장됩니다:
- `src/test/java/test_results/real_image_generation_*.txt` - 성공 결과
- `src/test/java/test_results/real_image_generation_failure_*.txt` - 실패 결과
- `src/test/java/test_results/multiple_real_images_*.txt` - 다중 생성 결과

### 3. 실제 이미지 확인
결과 파일에 포함된 URL을 웹브라우저에 입력하면 생성된 이미지를 볼 수 있습니다.

## 💰 비용 정보

### DALL-E 3 가격 (2024년 기준)
- 표준 품질 (1024×1024): $0.040 per image
- HD 품질 (1024×1024): $0.080 per image

### 예상 비용
- 단일 이미지 테스트: 약 $0.04
- 다중 이미지 테스트 (3개): 약 $0.12

## 🔍 트러블슈팅

### 1. "OpenAI API key not configured" 오류
- API 키가 올바르게 설정되었는지 확인
- 환경변수 이름이 정확한지 확인 (`OPENAI_API_KEY`)

### 2. "API quota exceeded" 오류
- OpenAI 계정의 사용량 제한을 확인
- 결제 정보가 올바르게 등록되었는지 확인

### 3. 네트워크 오류
- 인터넷 연결 상태 확인
- 프록시 설정이 있다면 Java 시스템 속성으로 설정

### 4. "Failed to generate image" 오류
- API 키의 유효성 확인
- OpenAI 서비스 상태 확인: https://status.openai.com/

## ✅ 성공 확인 방법

### 1. 콘솔에서 확인
```
=== 실제 이미지 생성 성공 ===
Book ID: REAL_TEST_12345678
Generated Image URL: https://oaidalleapiprodscus.blob.core.windows.net/private/...
URL 접근 가능 여부: 이 URL로 실제 이미지에 접근할 수 있습니다
```

### 2. 결과 파일에서 확인
파일 내용 중 다음 항목을 확인하세요:
- `URL 접근 가능 여부: YES (실제 DALL-E API로 생성된 이미지)`
- `생성된 이미지 URL: https://oaidalleapiprodscus.blob.core.windows.net/...`

### 3. 브라우저에서 이미지 확인
생성된 URL을 브라우저에 입력하여 실제 이미지를 확인하세요.

## 🔄 Mock vs Real 비교

| 구분 | Mock 서비스 | 실제 API |
|------|-------------|----------|
| URL 형식 | `https://mock-generated-image.example.com/...` | `https://oaidalleapiprodscus.blob.core.windows.net/...` |
| 접근 가능 | ❌ 불가능 (가짜 URL) | ✅ 가능 (실제 이미지) |
| 비용 | 무료 | 유료 ($0.04/image) |
| 생성 시간 | 즉시 (100ms 지연) | 5-10초 |
| 이미지 품질 | 없음 | 실제 AI 생성 이미지 |

현재까지 생성된 모든 테스트 결과는 Mock 서비스를 사용한 것이므로 URL에 접근할 수 없습니다. 실제 이미지를 생성하려면 위의 가이드를 따라 실제 API 키를 설정하고 `RealImageGenerationTest`를 실행하세요.
