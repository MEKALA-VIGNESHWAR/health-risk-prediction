# ============================================
# Multi-stage Dockerfile for RealTime Backend
# ============================================

# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and config
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Make wrapper executable and download deps
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

# Copy frontend (needed for maven-resources-plugin to embed in JAR)
COPY ../frontend ../frontend

# Copy source code
COPY src src

# Build fat JAR
RUN ./mvnw clean package -DskipTests -B

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/realtime-app-1.0.0.jar app.jar

# Expose port (configurable via PORT env var)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q -O- http://localhost:${PORT:-8080}/api/health || exit 1

# Run with production settings
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
