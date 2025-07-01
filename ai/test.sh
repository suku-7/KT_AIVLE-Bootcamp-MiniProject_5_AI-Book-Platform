#!/bin/bash

echo "🚀 AI 서비스 테스트 시작"
echo "========================"
echo ""

# 현재 위치를 AI 디렉토리로 이동
cd "$(dirname "$0")"

echo "📁 현재 디렉토리: $(pwd)"
echo ""

# API 키 확인
if [ -n "$OPENAI_API_KEY" ]; then
    echo "✅ API 키가 이미 설정되어 있습니다: ${OPENAI_API_KEY:0:10}..."
    echo "바로 테스트를 실행합니다."
    echo ""
    ./src/test/scripts/run_ai_test.sh
else
    echo "⚠️  API 키가 설정되지 않았습니다."
    echo "API 키 설정을 시작합니다..."
    echo ""
    ./src/test/scripts/setup_api_key.sh
fi
