# 빌드 후 dist가 있는 상태에서 사용하는 ultra-light Dockerfile
FROM webratio/nodejs-http-server

# 1. 디렉토리 마운트
VOLUME /tmp

# 2. 정적 파일 복사
ADD dist /opt/www

# 3. 실행 스크립트 복사
ADD run.sh /opt/run.sh

# 4. 환경변수 (빌드 시 사용되었어야 함)
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# 5. 타임존 설정 (옵션)
ENV TZ=Asia/Seoul

# 6. dist 존재 여부 확인 + 권한 설정
RUN test -d /opt/www || (echo "❌ /opt/www (dist) not found!" && exit 1) && \
    chmod +x /opt/run.sh

# 7. HTTP 포트
EXPOSE 8080

# 8. SPA 서버 실행
ENTRYPOINT ["sh", "/opt/run.sh"]