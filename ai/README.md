# AI 서비스 - 자동 커버 생성

## 📐 설계 인스트럭션: AI 표지 생성 & AI 문서 요약 (Bounded Context: ai)

### 1. 📦 Context: ai
해당 컨텍스트는 외부 시스템(AI-DALL·E, AI-GPT4.1-mini)과의 연동을 통해 책에 대한 자동 표지 생성, 내용 요약 기능을 제공하는 역할을 한다. 외부 Pub/Sub 이벤트인 도서등록신청됨이 트리거가 되며, 두 가지 policy에 의해 비동기적으로 처리된다.

### 2. 🎨 CoverDesign Aggregate
**📌 목적**
책의 제목(title)을 기반으로 AI 이미지 생성 시스템(DALL·E)을 통해 표지 이미지를 자동 생성하고 결과를 저장.

**📥 Input (정책 트리거: AI표지생성 Policy)**
```json
{
  "bookId": "string",
  "userId": "string", 
  "title": "string",
  "updatedAt": "datetime"
}
```

**🧠 내부 필드 (CoverDesign Aggregate)**
| 변수명 | 타입 | 설명 |
|--------|------|------|
| bookId | String | 책 고유 ID |
| userId | String | 생성 요청자 |
| title | String | 책 제목 |
| updatedAt | DateTime | 갱신 시각 |
| imageUrl | String | 생성된 표지 이미지 URL |
| generatedBy | String | 'AI-DALL-E' 고정 |
| createdAt | DateTime | 표지 생성 완료 시각 |

**📤 Output (이벤트 발행: AI표지생성됨)**
```json
{
  "bookId": "string",
  "userId": "string",
  "title": "string", 
  "imageUrl": "string",
  "generatedBy": "AI-DALL-E",
  "createdAt": "datetime"
}
```

**🔁 외부 연동**
- 외부 시스템: AI-DALL-E
- 처리 흐름: 입력받은 title을 프롬프트로 전환 → 이미지 생성 요청 → 생성된 이미지의 URL을 받아 저장 → AI표지생성됨 이벤트 발행

### 3. 🧠 ContentAnalyzer Aggregate
**📌 목적**
도서 내용을 AI 모델(GPT 기반)을 통해 요약하여 저장하고 전달.

**📥 Input (정책 트리거: AI문서요약 Policy)**
```json
{
  "bookId": "string",
  "userId": "string",
  "content": "string",
  "language": "KO | EN | ...",
  "maxLength": "int",
  "classificationType": "string",
  "requestedBy": "string"
}
```

**🧠 내부 필드 (ContentAnalyzer Aggregate)**
| 변수명 | 타입 | 설명 |
|--------|------|------|
| bookId | String | 책 고유 ID |
| userId | String | 요청 유저 ID |
| content | String | 요약할 원문 콘텐츠 |
| language | String | 콘텐츠 언어 |
| maxLength | Int | 요약 결과 최대 길이 |
| classificationType | String | 요약 목적 또는 분류 정보 (예: '기획서용', '리뷰용') |
| requestedBy | String | 요청 주체 (시스템 또는 유저) |

**📤 Output (이벤트 발행: AI문서요약됨)**
```json
{
  "bookId": "string",
  "userId": "string", 
  "content": "string",
  "language": "string",
  "maxLength": "int",
  "classificationType": "string",
  "requestedBy": "string"
}
```

**🔁 외부 연동**
- 외부 시스템: AI-GPT4.1-mini
- 처리 흐름: content, maxLength, language 등을 포함한 요약 요청 → 요약 결과 반환 → 저장 및 AI문서요약됨 이벤트 발행

### 4. 📡 이벤트 흐름 요약
| 이벤트 | 트리거 정책 | 결과 Aggregate | 발행 이벤트 |
|--------|-------------|----------------|-------------|
| 도서등록신청됨 | AI표지생성, AI문서요약 | CoverDesign, ContentAnalyzer | AI표지생성됨, AI문서요약됨 |

### 5. 🔗 외부 연동 상세
**AI-DALL·E**
- API Endpoint: /generate-image
- 입력: `{ prompt: title }`
- 출력: `{ imageUrl: string }`

**AI-GPT4.1-mini**
- API Endpoint: /summarize
- 입력: `{ content, language, maxLength, classificationType }`
- 출력: `{ summary: string }`

### 6. ✏️ 구현시 유의 사항
- Aggregate는 상태를 저장해야 하므로 persistence 계층과 연동되어야 함
- 이벤트 핸들러는 비동기 Pub/Sub 처리 방식으로 구성할 것
- 외부 API 연동 실패 시 재시도 또는 실패 이벤트 발행 고려
- 모델 결과물 검증 로직(예: imageUrl 유효성 검사, 요약 최소 길이 체크) 추가 필요

---

BookRegisted 이벤트를 수신하여:
1. DALL-E로 자동 커버 이미지 생성
2. GPT로 장르 자동 분류 (15개 장르)  
3. GPT로 장르 특성을 반영한 자동 요약 생성

## 설정

### API 키 설정

application.yml 
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
      # Cover Design 전용 설정
      cover:
        model: dall-e-3
        quality: standard
        size: 1024x1024
        style: vivid
      # AI Summary 전용 설정  
      summary:
        model: gpt-4
        temperature: 0.7
        max-tokens: 1000
      # Genre Classification 전용 설정
      genre:
        model: gpt-4
        temperature: 0.3
        max-tokens: 50

## 📊 실제 구현된 변수 및 이벤트 정리

### 🎯 입력 이벤트: BookRegisted
```json
{
  "bookId": "Long",
  "authorId": "Long", 
  "title": "String",
  "context": "String",
  "registration": "Boolean"
}
```

### 🎨 CoverDesign 처리 과정

#### 📥 입력 (BookRegisted 이벤트)
| 변수명 | 타입 | 설명 |
|--------|------|------|
| bookId | Long | 책 고유 ID |
| authorId | Long | 작가 ID |
| title | String | 책 제목 |
| context | String | 책 내용 |

#### 🏗️ CoverDesign 엔티티 필드
| 변수명 | 타입 | 설명 |
|--------|------|------|
| id | Long | CoverDesign 고유 ID |
| authorId | Long | 작가 ID |
| bookId | String | 책 ID (String 변환) |
| title | String | 책 제목 |
| imageUrl | String | DALL-E로 생성된 이미지 URL |
| generatedBy | String | "DALL-E-3" 고정값 |
| createdAt | Date | 생성 시간 |
| updatedAt | Date | 수정 시간 |

#### 📤 출력 이벤트: CoverCreated
```json
{
  "id": "Long",
  "authorId": "Long",
  "bookId": "String",
  "title": "String",
  "imageUrl": "String",
  "generatedBy": "String",
  "createdAt": "String"
}
```

### 📝 ContentAnalyzer 처리 과정

#### 📥 입력 (BookRegisted 이벤트)
동일한 BookRegisted 이벤트 사용

#### 🏗️ ContentAnalyzer 엔티티 필드
| 변수명 | 타입 | 설명 |
|--------|------|------|
| id | Long | ContentAnalyzer 고유 ID |
| bookId | String | 책 ID (String 변환) |
| context | String | 원본 책 내용 |
| summary | String | GPT로 생성된 요약 |
| language | String | "KO" 고정값 |
| maxLength | Integer | 500 고정값 |
| classificationType | String | GPT로 분류된 15개 장르 중 하나 |
| requestedBy | String | "AI-SYSTEM" 고정값 |

#### 📤 출력 이벤트: AiSummarized
```json
{
  "authorId": "Long",
  "bookId": "String",
  "context": "String", 
  "summary": "String",
  "language": "String",
  "maxLength": "Integer",
  "classificationType": "String",
  "requestedBy": "String"
}
```

### 🎯 통합 처리: AiProcessTracker

#### 🏗️ AiProcessTracker 엔티티 필드
| 변수명 | 타입 | 설명 |
|--------|------|------|
| id | Long | Tracker 고유 ID |
| authorId | Long | 작가 ID |
| bookId | String | 책 ID |
| title | String | 책 제목 |
| contentAnalysisCompleted | Boolean | 내용 분석 완료 여부 |
| coverGenerationCompleted | Boolean | 커버 생성 완료 여부 |
| summary | String | 생성된 요약 |
| classificationType | String | 분류된 장르 |
| language | String | 언어 설정 |
| maxLength | Integer | 최대 길이 |
| imageUrl | String | 커버 이미지 URL |
| generatedBy | String | 생성 도구명 |
| createdAt | Date | 생성 시간 |
| completedAt | Date | 완료 시간 |

#### 📤 최종 출력 이벤트: BookAiProcessCompleted
```json
{
  "authorId": "Long",
  "bookId": "String",
  "title": "String",
  "summary": "String",
  "classificationType": "String", 
  "language": "String",
  "maxLength": "Integer",
  "imageUrl": "String",
  "generatedBy": "String",
  "coverCreatedAt": "Date",
  "contentAnalysisCompleted": "Boolean",
  "coverGenerationCompleted": "Boolean", 
  "completedAt": "Date"
}
```

### 📋 지원하는 15개 장르 목록
1. 현대소설 (Contemporary Fiction)
2. 로맨스 (Romance)
3. 판타지 / SF (Fantasy / Sci-Fi)
4. 추리 / 스릴러 / 범죄 (Mystery / Thriller / Crime)
5. 공포 / 호러 (Horror)
6. 역사소설 (Historical Fiction)
7. 청소년 / 청춘소설 (Young Adult)
8. 에세이 / 수필 (Essay / Memoir)
9. 인문 / 철학 / 종교 (Humanities / Philosophy / Religion)
10. 심리 / 자기계발 (Psychology / Self-help)
11. 사회 / 정치 / 시사 (Society / Politics)
12. 경제 / 경영 / 투자 (Business / Economics)
13. 과학 / 기술 / IT (Science / Technology)
14. 아동 / 그림책 (Children / Picture Books)
15. 라이프스타일 / 취미 / 여행 (Lifestyle / Hobby / Travel)

### 🔄 전체 처리 플로우
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

### ⚙️ AI 모델 설정 (application.yml)
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      # Cover Design 전용 설정
      cover:
        model: dall-e-3
        quality: standard
        size: 1024x1024
        style: vivid
      # AI Summary 전용 설정  
      summary:
        model: gpt-4
        temperature: 0.7
        max-tokens: 1000
      # Genre Classification 전용 설정
      genre:
        model: gpt-4
        temperature: 0.3
        max-tokens: 50
```

---

