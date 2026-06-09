# ==============================
# Stage 1: Build the application
# ==============================
FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /app

# Copy Gradle wrapper
COPY gradlew .
COPY gradle gradle

# Copy build files
COPY build.gradle settings.gradle ./

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Build the project (skip tests for faster build)
RUN ./gradlew bootJar -x test


# ==============================
# Stage 2: Run the application
# ==============================
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# IMPORTANT: adjust jar name if needed
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose Spring Boot port (change if you use another port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]