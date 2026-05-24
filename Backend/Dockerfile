# --- Stage 1: build ---
FROM maven:3.8-eclipse-temurin-11 AS builder
WORKDIR /app

# Copia o pom primeiro para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# --- Stage 2: runtime ---
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/transfer-scheduler-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
