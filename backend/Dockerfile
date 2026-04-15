# Backend Dockerfile
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy Maven wrapper and configuration
COPY .mvn /app/.mvn
COPY mvnw /app/mvnw
COPY pom.xml /app/pom.xml

# Download dependencies
RUN chmod +x /app/mvnw && /app/mvnw dependency:resolve

# Copy source code
COPY src /app/src

# Build the application
RUN /app/mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=0 /app/target/realtime-app-1.0.0.jar /app/realtime-app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -q -O- http://localhost:8080 || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/realtime-app.jar"]
