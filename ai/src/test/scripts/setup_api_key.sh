#!/bin/bash

echo "OpenAI API 키 환경변수 설정 도구"
echo "================================"
echo ""
echo "이 스크립트는 OpenAI API 키를 환경변수로 설정합니다."
echo "API 키는 OpenAI 웹사이트(https://platform.openai.com/api-keys)에서 발급받을 수 있습니다."
echo ""

read -p "OpenAI API 키를 입력하세요: " api_key

if [ -z "$api_key" ]; then
    echo "API 키가 입력되지 않았습니다."
    exit 1
fi

echo ""
echo "API 키를 환경변수에 설정 중..."
export OPENAI_API_KEY="$api_key"

echo "API 키가 설정되었습니다."
echo "현재 세션에서만 유효합니다."
echo ""

echo "설정된 API 키 확인:"
if [ -n "$OPENAI_API_KEY" ]; then
    echo "- 환경변수 설정됨: ${OPENAI_API_KEY:0:10}..."
else
    echo "- 환경변수 설정되지 않음"
fi

echo ""
read -p "이제 AI 테스트를 실행하시겠습니까? (y/n): " run_test

if [[ "$run_test" =~ ^[Yy]$ ]]; then
    echo ""
    echo "AI 테스트 실행 중..."
    # 현재 디렉토리가 scripts이므로 같은 디렉토리의 run_ai_test.sh 실행
    cd "$(dirname "$0")"
    ./run_ai_test.sh
else
    echo ""
    echo "수동으로 테스트를 실행하려면 다음 명령어를 사용하세요:"
    echo "./run_ai_test.sh"
fi
