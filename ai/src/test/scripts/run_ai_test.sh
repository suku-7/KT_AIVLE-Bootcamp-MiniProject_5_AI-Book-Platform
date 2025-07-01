#!/bin/bash

echo "========================================"
echo "AI 서비스 통합 테스트 실행"
echo "========================================"

# scripts 디렉토리에서 AI 디렉토리로 이동 (3단계 상위)
cd "$(dirname "$0")/../../.."

echo ""
echo "📁 현재 작업 디렉토리: $(pwd)"

echo ""
echo "1. Maven 의존성 확인 및 컴파일..."
./apache-maven-3.9.9/bin/mvn clean compile

if [ $? -ne 0 ]; then
    echo "Maven 컴파일 실패!"
    exit 1
fi

echo ""
echo "2. 테스트 컴파일..."
./apache-maven-3.9.9/bin/mvn test-compile

if [ $? -ne 0 ]; then
    echo "테스트 컴파일 실패!"
    exit 1
fi

echo ""
echo "3. OpenAI API 키 확인..."
if [ -n "$OPENAI_API_KEY" ]; then
    echo "✅ API 키가 설정되어 있습니다: ${OPENAI_API_KEY:0:10}..."
    echo "실제 OpenAI API를 사용한 테스트를 실행합니다."
    TEST_MODE="REAL"
else
    echo "⚠️  API 키가 설정되지 않았습니다."
    echo "Mock/시뮬레이션 모드로 실행됩니다."
    TEST_MODE="MOCK"
fi

echo ""
echo "4. AI 통합 테스트 실행..."
./apache-maven-3.9.9/bin/mvn exec:java -Dexec.mainClass="thminiprojthebook.AiIntegrationTest" -Dexec.classpathScope="test"

if [ "$TEST_MODE" = "REAL" ]; then
    echo ""
    echo "5. 실제 이미지 생성 테스트 실행..."
    ./apache-maven-3.9.9/bin/mvn exec:java -Dexec.mainClass="thminiprojthebook.RealImageGenerationTest" -Dexec.classpathScope="test"
fi

echo ""
echo "6. JUnit 테스트 실행..."
./apache-maven-3.9.9/bin/mvn test -Dtest=AiServiceIndependentTest

echo ""
echo "========================================"
echo "테스트 완료!"
echo "========================================"

echo ""
echo "결과 파일들:"
if [ -f "src/test/results/ai_service_standalone_test_results.txt" ]; then
    echo "- src/test/results/ai_service_standalone_test_results.txt (독립 실행 테스트 결과)"
fi
if [ -f "src/test/results/real_image_generation_"*.txt ]; then
    echo "- src/test/results/real_image_generation_*.txt (실제 이미지 생성 결과)"
fi

echo ""
echo "테스트 결과 요약:"
if [ "$TEST_MODE" = "REAL" ]; then
    echo "🎉 실제 OpenAI API를 사용한 테스트가 완료되었습니다!"
    echo "📂 src/test/results/ 폴더에서 결과를 확인하세요."
    echo "🌐 생성된 이미지 URL을 브라우저에서 열어보세요."
else
    echo "⚠️  Mock 모드로 테스트가 완료되었습니다."
    echo "실제 테스트를 원한다면 ../setup_api_key.sh를 실행하세요."
fi

echo ""
echo "테스트 결과 미리보기:"
if [ -f "src/test/results/ai_service_standalone_test_results.txt" ]; then
    echo "--- Standalone 테스트 결과 (처음 20줄) ---"
    head -20 src/test/results/ai_service_standalone_test_results.txt
fi
