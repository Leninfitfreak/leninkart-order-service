# Stage 1: Build the app
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Cache dependencies for faster rebuilds
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -B -T1C -DskipTests clean package

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY otel/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -javaagent:/otel/opentelemetry-javaagent.jar -Dotel.exporter.otlp.endpoint=${OTEL_EXPORTER_OTLP_ENDPOINT:-http://otel-collector:4318} -Dotel.exporter.otlp.protocol=${OTEL_EXPORTER_OTLP_PROTOCOL:-http/protobuf} -Dotel.service.name=${OTEL_SERVICE_NAME:-order-service} -jar /app/app.jar"]
