# Этап сборки
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/email-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
