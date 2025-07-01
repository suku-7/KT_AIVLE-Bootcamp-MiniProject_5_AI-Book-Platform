@echo off
echo ================================================
echo AI 서비스 실제 API 테스트 - 통합 실행 도구
echo ================================================

echo.
echo 이 스크립트는 다음을 수행합니다:
echo 1. OpenAI API 키 설정
echo 2. 환경 검증
echo 3. 실제 API 호출 테스트
echo 4. 결과 분석 및 저장

echo.
set /p continue="계속 진행하시겠습니까? (y/n): "
if /i not "%continue%"=="y" (
    echo 테스트를 취소했습니다.
    pause
    exit /b 0
)

echo.
echo ================================================
echo 1단계: OpenAI API 키 설정
echo ================================================

if defined OPENAI_API_KEY (
    echo 기존 API 키가 감지되었습니다: %OPENAI_API_KEY:~0,10%...
    set /p use_existing="기존 API 키를 사용하시겠습니까? (y/n): "
    if /i "%use_existing%"=="y" goto :test_start
)

:set_new_key
echo.
echo OpenAI API 키를 입력해주세요.
echo 키는 https://platform.openai.com/api-keys 에서 발급받을 수 있습니다.
echo.
set /p api_key="API 키: "

if "%api_key%"=="" (
    echo API 키가 입력되지 않았습니다.
    pause
    exit /b 1
)

if not "%api_key:~0,3%"=="sk-" (
    echo ⚠️  경고: 올바른 OpenAI API 키 형식이 아닙니다. (sk-로 시작해야 함)
    set /p force_continue="계속 진행하시겠습니까? (y/n): "
    if /i not "%force_continue%"=="y" (
        goto :set_new_key
    )
)

set OPENAI_API_KEY=%api_key%
echo ✅ API 키가 설정되었습니다.

:test_start
echo.
echo ================================================
echo 2단계: 환경 검증
echo ================================================

echo Maven 컴파일 중...
call .\apache-maven-3.9.9\bin\mvn clean compile -q
if %errorlevel% neq 0 (
    echo ❌ Maven 컴파일 실패
    pause
    exit /b 1
)

call .\apache-maven-3.9.9\bin\mvn test-compile -q
if %errorlevel% neq 0 (
    echo ❌ 테스트 컴파일 실패
    pause
    exit /b 1
)

echo ✅ 컴파일 완료

echo.
echo ================================================
echo 3단계: 실제 API 호출 테스트 실행
echo ================================================

echo 테스트 시작 시간: %date% %time%
echo.

call .\apache-maven-3.9.9\bin\mvn exec:java -Dexec.mainClass="thminiprojthebook.AiServiceStandaloneTest" -Dexec.classpathScope="test" -q

echo.
echo ================================================
echo 4단계: 결과 분석
echo ================================================

if exist "src\test\results\ai_service_standalone_test_results.txt" (
    echo ✅ 테스트 결과 파일이 생성되었습니다.
    echo 📁 위치: src\test\results\ai_service_standalone_test_results.txt
    
    echo.
    echo 📊 결과 요약:
    findstr /C:"API Key" /C:"응답 시간" /C:"API 호출" /C:"결과:" src\test\results\ai_service_standalone_test_results.txt | findstr /V "구성:"
    
    echo.
    set /p view_full="전체 결과를 보시겠습니까? (y/n): "
    if /i "%view_full%"=="y" (
        echo.
        echo ================================================
        echo 전체 테스트 결과:
        echo ================================================
        type src\test\results\ai_service_standalone_test_results.txt
    )
) else (
    echo ❌ 테스트 결과 파일을 찾을 수 없습니다.
)

echo.
echo ================================================
echo 추가 테스트 옵션
echo ================================================

set /p run_junit="JUnit 테스트도 실행하시겠습니까? (y/n): "
if /i "%run_junit%"=="y" (
    echo JUnit 테스트 실행 중...
    call .\apache-maven-3.9.9\bin\mvn test -Dtest=AiServiceIndependentTest -q
    
    if exist "src\test\results\ai_service_test_results.txt" (
        echo ✅ JUnit 테스트 결과: src\test\results\ai_service_test_results.txt
    )
)

echo.
echo ================================================
echo 테스트 완료!
echo ================================================
echo 📁 결과 파일들:
if exist "src\test\results\ai_service_standalone_test_results.txt" echo   - src\test\results\ai_service_standalone_test_results.txt
if exist "src\test\results\ai_service_test_results.txt" echo   - src\test\results\ai_service_test_results.txt

echo.
echo 💡 팁:
echo - API 키를 안전하게 관리하세요
echo - 결과 파일을 Git에 커밋하지 마세요
echo - 비용을 모니터링하세요 (OpenAI 대시보드)

echo.
pause
