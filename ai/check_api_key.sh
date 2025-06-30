#!/bin/bash

echo "=== OpenAI API 키 설정 상태 확인 ==="
echo

# 결과 디렉토리 생성
mkdir -p src/test/java/test_results

# 현재 시간
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")

# 환경변수 확인
echo "=== 환경변수 확인 ==="
if [ -n "$OPENAI_API_KEY" ]; then
    # API 키가 설정됨 - 마스킹해서 표시
    MASKED_KEY="${OPENAI_API_KEY:0:7}...${OPENAI_API_KEY: -4}"
    echo "✅ OPENAI_API_KEY 환경변수가 설정되어 있습니다: $MASKED_KEY"
    API_STATUS="REAL_API_AVAILABLE"
    API_MESSAGE="실제 DALL-E API 호출이 가능합니다"
else
    echo "❌ OPENAI_API_KEY 환경변수가 설정되지 않았습니다"
    API_STATUS="MOCK_ONLY"
    API_MESSAGE="Mock 서비스만 사용 가능합니다"
fi

echo
echo "=== 현재 상태 ==="
echo "상태: $API_STATUS"
echo "설명: $API_MESSAGE"

# 결과를 파일로 저장
RESULT_FILE="src/test/java/test_results/api_key_status_$TIMESTAMP.txt"

cat > "$RESULT_FILE" << EOF
=== OpenAI API 키 설정 상태 확인 결과 ===
확인 시간: $(date)

=== 환경변수 상태 ===
EOF

if [ -n "$OPENAI_API_KEY" ]; then
    cat >> "$RESULT_FILE" << EOF
✅ OPENAI_API_KEY: 설정됨 ($MASKED_KEY)
상태: 실제 API 호출 가능
비용: 이미지당 약 $0.04 발생

=== 실제 이미지 생성 방법 ===
1. RealImageGenerationTest.java 실행
2. 또는 터미널에서 다음 명령 실행:
   cd /c/Github/thminiprojthebook/ai
   java -cp target/classes thminiprojthebook.integration.RealImageGenerationTest

=== 중요 사항 ===
- 실제 API 사용 시 비용이 발생합니다
- 생성된 URL은 실제 이미지에 접근 가능합니다
- 이미지는 일정 기간 후 만료될 수 있습니다
EOF
else
    cat >> "$RESULT_FILE" << EOF
❌ OPENAI_API_KEY: 설정되지 않음
상태: Mock 서비스만 사용 가능

=== 설정 방법 ===
Windows PowerShell:
  \$env:OPENAI_API_KEY="your-actual-api-key-here"

Windows CMD:
  set OPENAI_API_KEY=your-actual-api-key-here

Linux/Mac:
  export OPENAI_API_KEY="your-actual-api-key-here"

=== Mock vs Real 비교 ===
Mock 서비스:
- URL: https://mock-generated-image.example.com/...
- 접근 가능: ❌ 불가능 (가짜 URL)
- 비용: 무료
- 이미지: 없음

실제 API:
- URL: https://oaidalleapiprodscus.blob.core.windows.net/...
- 접근 가능: ✅ 가능 (실제 이미지)
- 비용: \$0.04/image
- 이미지: 실제 AI 생성 이미지
EOF
fi

cat >> "$RESULT_FILE" << EOF

=== 현재까지의 테스트 결과 ===
지금까지 생성된 모든 테스트 결과는 Mock 서비스를 사용한 것입니다.
따라서 결과 파일에 포함된 이미지 URL들은 실제로 접근할 수 없습니다.

실제 이미지를 생성하려면:
1. OpenAI API 키를 설정하세요
2. RealImageGenerationTest를 실행하세요
3. 생성된 URL로 실제 이미지를 확인하세요
EOF

echo
echo "📄 결과가 저장되었습니다: $RESULT_FILE"
echo

# 파일 내용 간략히 표시
echo "=== 저장된 결과 요약 ==="
head -10 "$RESULT_FILE"
echo "..."
echo "(전체 내용은 $RESULT_FILE 파일에서 확인하세요)"

echo
echo "=== API 키 확인 완료 ==="
