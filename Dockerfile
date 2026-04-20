# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy the entire project so maven can find both frontend and backend
COPY . .

# Run maven from the backend directory where pom.xml is located
WORKDIR /app/backend
RUN chmod +x mvnw && ./mvnw clean package -DskipTests -B


# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
