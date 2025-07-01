#!/bin/bash

echo "================================================"
echo "AI 서비스 실제 API 테스트 - 통합 실행 도구"
echo "================================================"

echo ""
echo "이 스크립트는 다음을 수행합니다:"
echo "1. OpenAI API 키 설정"
echo "2. 환경 검증"
echo "3. 실제 API 호출 테스트"
echo "4. 결과 분석 및 저장"

echo ""
read -p "계속 진행하시겠습니까? (y/n): " continue
if [[ ! "$continue" =~ ^[Yy]$ ]]; then
    echo "테스트를 취소했습니다."
    exit 0
fi

echo ""
echo "================================================"
echo "1단계: OpenAI API 키 설정"
echo "================================================"

if [ -n "$OPENAI_API_KEY" ]; then
    echo "기존 API 키가 감지되었습니다: ${OPENAI_API_KEY:0:10}..."
    read -p "기존 API 키를 사용하시겠습니까? (y/n): " use_existing
    if [[ "$use_existing" =~ ^[Yy]$ ]]; then
        goto_test=true
    fi
fi

if [ "$goto_test" != true ]; then
    echo ""
    echo "OpenAI API 키를 입력해주세요."
    echo "키는 https://platform.openai.com/api-keys 에서 발급받을 수 있습니다."
    echo ""
    read -p "API 키: " api_key
    
    if [ -z "$api_key" ]; then
        echo "API 키가 입력되지 않았습니다."
        exit 1
    fi
    
    if [[ ! "$api_key" =~ ^sk- ]]; then
        echo "⚠️  경고: 올바른 OpenAI API 키 형식이 아닙니다. (sk-로 시작해야 함)"
        read -p "계속 진행하시겠습니까? (y/n): " force_continue
        if [[ ! "$force_continue" =~ ^[Yy]$ ]]; then
            echo "테스트를 취소했습니다."
            exit 1
        fi
    fi
    
    export OPENAI_API_KEY="$api_key"
    echo "✅ API 키가 설정되었습니다."
fi

echo ""
echo "================================================"
echo "2단계: 환경 검증"
echo "================================================"

echo "Maven 컴파일 중..."
if ! ./apache-maven-3.9.9/bin/mvn clean compile -q; then
    echo "❌ Maven 컴파일 실패"
    exit 1
fi

if ! ./apache-maven-3.9.9/bin/mvn test-compile -q; then
    echo "❌ 테스트 컴파일 실패"
    exit 1
fi

echo "✅ 컴파일 완료"

echo ""
echo "================================================"
echo "3단계: 실제 API 호출 테스트 실행"
echo "================================================"

echo "테스트 시작 시간: $(date)"
echo ""

./apache-maven-3.9.9/bin/mvn exec:java -Dexec.mainClass="thminiprojthebook.AiServiceStandaloneTest" -Dexec.classpathScope="test" -q

echo ""
echo "================================================"
echo "4단계: 결과 분석"
echo "================================================"

if [ -f "src/test/results/ai_service_standalone_test_results.txt" ]; then
    echo "✅ 테스트 결과 파일이 생성되었습니다."
    echo "📁 위치: src/test/results/ai_service_standalone_test_results.txt"
    
    echo ""
    echo "📊 결과 요약:"
    grep -E "(API Key|응답 시간|API 호출|결과:)" src/test/results/ai_service_standalone_test_results.txt | grep -v "구성:"
    
    echo ""
    read -p "전체 결과를 보시겠습니까? (y/n): " view_full
    if [[ "$view_full" =~ ^[Yy]$ ]]; then
        echo ""
        echo "================================================"
        echo "전체 테스트 결과:"
        echo "================================================"
        cat src/test/results/ai_service_standalone_test_results.txt
    fi
else
    echo "❌ 테스트 결과 파일을 찾을 수 없습니다."
fi

echo ""
echo "================================================"
echo "추가 테스트 옵션"
echo "================================================"

read -p "JUnit 테스트도 실행하시겠습니까? (y/n): " run_junit
if [[ "$run_junit" =~ ^[Yy]$ ]]; then
    echo "JUnit 테스트 실행 중..."
    ./apache-maven-3.9.9/bin/mvn test -Dtest=AiServiceIndependentTest -q
    
    if [ -f "src/test/results/ai_service_test_results.txt" ]; then
        echo "✅ JUnit 테스트 결과: src/test/results/ai_service_test_results.txt"
    fi
fi

echo ""
echo "================================================"
echo "테스트 완료!"
echo "================================================"
echo "📁 결과 파일들:"
[ -f "src/test/results/ai_service_standalone_test_results.txt" ] && echo "  - src/test/results/ai_service_standalone_test_results.txt"
[ -f "src/test/results/ai_service_test_results.txt" ] && echo "  - src/test/results/ai_service_test_results.txt"

echo ""
echo "💡 팁:"
echo "- API 키를 안전하게 관리하세요"
echo "- 결과 파일을 Git에 커밋하지 마세요"
echo "- 비용을 모니터링하세요 (OpenAI 대시보드)"

echo ""
read -p "Press Enter to continue..."
