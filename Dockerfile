# =========================
# Stage 1: Build (Maven)
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first (for caching dependencies)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN mvn clean package -DskipTests


# =========================
# Stage 2: Run (JRE)
# =========================
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (change if needed)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]