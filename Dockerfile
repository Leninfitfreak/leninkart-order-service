# Stage 1: Build the app
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Cache dependencies for faster rebuilds
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -B -DskipTests clean package

# Stage 2: Runtime image (smaller JRE instead of full JDK)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
