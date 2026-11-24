
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Configure alternative Maven Central mirror
COPY settings.xml /root/.m2/settings.xml

COPY MealManagement/pom.xml .
COPY MealManagement/src src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
