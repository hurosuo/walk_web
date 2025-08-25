FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew

COPY . .

RUN ./gradlew build -x test --no-daemon

CMD ["java", "-jar", "build/libs/walk_web-0.0.1-SNAPSHOT.jar"]