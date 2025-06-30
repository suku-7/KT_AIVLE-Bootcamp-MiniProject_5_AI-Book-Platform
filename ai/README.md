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
환경변수로 OpenAI API 키를 설정하세요:
```bash
export OPENAI_API_KEY=sk-your-actual-api-key-here
```

[OpenAI Platform](https://platform.openai.com/api-keys)에서 API 키를 발급받으세요.

## Running in local development environment

```bash
export OPENAI_API_KEY=sk-your-actual-api-key-here
mvn spring-boot:run
```

## Packaging and Running in docker environment

```
mvn package -B -DskipTests
docker build -t username/ai:v1 .
docker run username/ai:v1
```

## Push images and running in Kubernetes

```
docker login 
# in case of docker hub, enter your username and password

docker push username/ai:v1
```

Edit the deployment.yaml under the /kubernetes directory:
```
    spec:
      containers:
        - name: ai
          image: username/ai:latest   # change this image name
          ports:
            - containerPort: 8080

```

Apply the yaml to the Kubernetes:
```
kubectl apply -f kubernetes/deployment.yaml
```

See the pod status:
```
kubectl get pods -l app=ai
```

If you have no problem, you can connect to the service by opening a proxy between your local and the kubernetes by using this command:
```
# new terminal
kubectl port-forward deploy/ai 8080:8080

# another terminal
http localhost:8080
```

If you have any problem on running the pod, you can find the reason by hitting this:
```
kubectl logs -l app=ai
```

Following problems may be occurred:

1. ImgPullBackOff:  Kubernetes failed to pull the image with the image name you've specified at the deployment.yaml. Please check your image name and ensure you have pushed the image properly.
1. CrashLoopBackOff: The spring application is not running properly. If you didn't provide the kafka installation on the kubernetes, the application may crash. Please install kafka firstly:

https://labs.msaez.io/#/courses/cna-full/full-course-cna/ops-utility

