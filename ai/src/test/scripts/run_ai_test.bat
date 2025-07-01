@echo off
echo ========================================
echo AI 서비스 독립 기능 테스트 실행
echo ========================================

cd /d "c:\Github\thminiprojthebook\ai"

echo.
echo 1. Maven 컴파일 수행...
call mvn clean compile

if %errorlevel% neq 0 (
    echo Maven 컴파일 실패!
    pause
    exit /b 1
)

echo.
echo 2. 테스트 클래스 컴파일...
call mvn test-compile

if %errorlevel% neq 0 (
    echo 테스트 컴파일 실패!
    pause
    exit /b 1
)

echo.
echo 3. 독립 실행 테스트 수행...
echo OpenAI API 키 설정 상태: 
if defined OPENAI_API_KEY (
    echo API 키가 설정되어 있습니다.
) else (
    echo API 키가 설정되지 않았습니다. 시뮬레이션 모드로 실행됩니다.
)

echo.
echo 테스트 실행 중...
java -cp "target/classes;target/test-classes;%USERPROFILE%\.m2\repository\org\springframework\spring-web\5.2.7.RELEASE\spring-web-5.2.7.RELEASE.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.11.0\jackson-databind-2.11.0.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.11.0\jackson-core-2.11.0.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.11.0\jackson-annotations-2.11.0.jar" thminiprojthebook.AiServiceStandaloneTest

echo.
echo 4. JUnit 테스트 실행...
call mvn test -Dtest=AiServiceIndependentTest

echo.
echo ========================================
echo 테스트 완료!
echo ========================================
echo.
echo 결과 파일들:
if exist "ai_service_standalone_test_results.txt" (
    echo - ai_service_standalone_test_results.txt (독립 실행 테스트 결과)
)
if exist "ai_service_test_results.txt" (
    echo - ai_service_test_results.txt (JUnit 테스트 결과)
)

echo.
pause
