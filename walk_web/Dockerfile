FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# GitHub Actions가 업로드한 JAR을 이 이름으로 둘 예정
COPY walk_web-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75" \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# 비루트 실행
RUN useradd -u 10001 -m appuser
USER 10001

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=$SERVER_PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
