# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Make executable & download deps
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

# Copy source
COPY src src

# Build jar
RUN ./mvnw clean package -DskipTests -B


# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java","-Xmx512m","-jar","app.jar"]