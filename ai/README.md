# AI 서비스 - 자동 커버 생성 & 내용 요약

## 📖 개요

이 모듈은 책 등록 시 AI를 활용하여 자동으로 커버 이미지를 생성하고 내용을 요약하는 서비스입니다.

## 🎯 주요 기능

- **🎨 자동 커버 생성**: DALL-E 3를 사용한 책 표지 이미지 자동 생성
- **📝 내용 요약**: GPT-4를 사용한 책 내용 자동 요약
- **📚 장르 분류**: 15개 장르 중 자동 분류

## 🚀 빠른 시작

```bash
# Linux/Mac
./test.sh

# Windows
test.bat
```

## 📋 상세 가이드

모든 테스트 실행 방법과 결과 확인은 **[FINAL_TEST_SUMMARY.md](./FINAL_TEST_SUMMARY.md)**를 참조하세요.

## 🏗️ 아키텍처

### 이벤트 기반 처리
```
BookRegisted 이벤트 수신
    ↓
    ├── ContentAnalyzer.aiSummarize()
    │   ├── GPT로 요약 생성
    │   ├── GPT로 장르 분류 (15개 중 선택)
    │   └── AiSummarized 이벤트 발행
    │
    └── CoverDesign.autoCoverGeneratePolicy()
        ├── DALL-E로 커버 이미지 생성
        └── CoverCreated 이벤트 발행
                ↓
        AiProcessTracker가 두 프로세스 완료 감지
                ↓
        BookAiProcessCompleted 이벤트 발행
                ↓
        LibraryPlatform에서 완성된 도서 출간
```

## ⚙️ 설정

### API 키 설정
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      cover:
        model: dall-e-3
        quality: standard
        size: 1024x1024
        style: vivid
      summary:
        model: gpt-4
        temperature: 0.7
        max-tokens: 1000
      genre:
        model: gpt-4
        temperature: 0.3
        max-tokens: 50
```

## 📁 프로젝트 구조

```
src/
├── main/java/thminiprojthebook/
│   ├── domain/           # 도메인 모델
│   ├── service/          # AI 서비스 (DalleService, GptService)
│   ├── policy/           # 이벤트 정책
│   └── infra/            # 외부 연동
└── test/                 # 테스트 (상세한 가이드는 FINAL_TEST_SUMMARY.md 참조)
```

## 💰 비용 정보

- **DALL-E 3**: $0.04 per image
- **GPT-4**: ~$0.01-0.03 per summary
- **테스트 실행**: ~$0.06-0.08 per run

---

**📖 자세한 사용법과 테스트 가이드는 [FINAL_TEST_SUMMARY.md](./FINAL_TEST_SUMMARY.md)를 확인하세요.**
