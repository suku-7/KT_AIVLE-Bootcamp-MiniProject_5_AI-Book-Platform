@echo off
echo 🚀 AI 서비스 테스트 시작
echo ========================
echo.

cd /d %~dp0

echo 📁 현재 디렉토리: %cd%
echo.

if defined OPENAI_API_KEY (
    echo ✅ API 키가 이미 설정되어 있습니다.
    echo 바로 테스트를 실행합니다.
    echo.
    call src\test\scripts\run_ai_test.bat
) else (
    echo ⚠️  API 키가 설정되지 않았습니다.
    echo API 키 설정을 시작합니다...
    echo.
    call src\test\scripts\setup_api_key.bat
)
