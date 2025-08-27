# 최신 JAR을 app.jar로 링크(이미 app.jar면 패스)
LATEST_JAR="$(ls -t *.jar 2>/dev/null | head -n1 || true)"
if [ -n "${LATEST_JAR}" ] && [ "${LATEST_JAR}" != "app.jar" ]; then
  ln -sf "${LATEST_JAR}" app.jar
fi

# .dockerignore가 *.jar를 막고 있으면 제거
[ -f .dockerignore ] && sed -i '/\*\.jar/d' .dockerignore || true
# 윈도우 줄바꿈 방지
[ -f .env ] && sed -i 's/\r$//' .env || true

echo "[deploy] build & run"
if docker compose version >/dev/null 2>&1; then
  docker compose up -d --build
else
  echo "[deploy] compose not found -> fallback to docker run"
  docker build -t walk-web:latest .
  docker rm -f walk-web 2>/dev/null || true
  docker run -d --name walk-web \
    --restart unless-stopped \
    --env-file .env \
    -p 80:8080 \
    walk-web:latest
fi

docker image prune -f || true
echo "[deploy] done"
