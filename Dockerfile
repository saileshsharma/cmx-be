# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Cache deps
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine

# Optional: tiny tool for healthcheck
RUN apk add --no-cache wget

# Run as non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Container-aware JVM tuning (tweak as needed)
ENV JAVA_OPTS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0 -XX:+UseStringDeduplication" \
    TZ=Asia/Singapore \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Healthcheck (requires /actuator/health to be public OR remove this line)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
