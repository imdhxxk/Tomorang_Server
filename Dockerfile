# ---- Build Stage ----
FROM gradle:8-jdk17-alpine AS build
WORKDIR /app

# 의존성 캐시 최적화
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true

# 소스 복사 및 빌드 (테스트 제외)
COPY src src
RUN gradle bootJar --no-daemon -x test

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
