#!/bin/bash

# 한글 출력 문제 해결을 위한 터미널 설정 스크립트

echo "=== 터미널 한글 인코딩 설정 가이드 ==="
echo ""

echo "1. Windows에서 한글 출력 문제 해결 방법:"
echo ""

echo "방법 1: Git Bash 설정"
echo "- Git Bash를 열고 다음 명령어를 실행:"
echo "  export LANG=ko_KR.UTF-8"
echo "  export LC_ALL=ko_KR.UTF-8"
echo ""

echo "방법 2: Windows Terminal 사용"
echo "- Windows Terminal을 설치하고 사용"
echo "- 설정 > 프로필 > 기본값 > 모양 > 글꼴 > Consolas 또는 D2Coding"
echo ""

echo "방법 3: 명령 프롬프트(cmd) 사용"
echo "- cmd를 열고 다음 명령어 실행:"
echo "  chcp 65001"
echo ""

echo "방법 4: PowerShell 사용"
echo "- PowerShell을 열고 다음 명령어 실행:"
echo "  [Console]::OutputEncoding = [System.Text.Encoding]::UTF8"
echo ""

echo "=== 현재 시스템 정보 ==="
echo "운영체제: $(uname -s)"
echo "현재 LANG: ${LANG:-'설정되지 않음'}"
echo "현재 LC_ALL: ${LC_ALL:-'설정되지 않음'}"
echo ""

echo "=== 인코딩 설정 적용 ==="
echo "현재 세션에 UTF-8 인코딩을 적용합니다..."

export LANG=ko_KR.UTF-8
export LC_ALL=ko_KR.UTF-8

echo "✅ 인코딩 설정 완료"
echo "LANG=$LANG"
echo "LC_ALL=$LC_ALL"
echo ""

echo "=== 한글 테스트 ==="
echo "한글이 제대로 출력되는지 확인: 안녕하세요! 테스트입니다."
echo "API 키 테스트: sk-proj-abc...123"
echo "이모지 테스트: 🚀 ✅ ❌ 📂 🌐"
echo ""

echo "이제 AI 테스트를 다시 실행해보세요:"
echo "./test.sh"
