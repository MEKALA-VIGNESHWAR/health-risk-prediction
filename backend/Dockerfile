# ============================================
# Multi-stage Dockerfile for RealTime
# ============================================

# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

# Working directory for the entire app structure
WORKDIR /app

# Copy the wrapper and backend files
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/pom.xml backend/

WORKDIR /app/backend

# Make wrapper executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

# Copy the frontend into /app/frontend so the maven plugin can find it at ../frontend
COPY frontend /app/frontend

# Copy backend source code
COPY backend/src src

# Build fat JAR
RUN ./mvnw clean package -DskipTests -B

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/backend/target/realtime-app-1.0.0.jar app.jar

# Expose port (configurable via PORT env var)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q -O- http://localhost:${PORT:-8080}/api/health || exit 1

# Run with production settings
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
