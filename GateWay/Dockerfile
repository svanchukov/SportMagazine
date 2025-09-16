# Этап сборки
FROM maven:3.9.9-eclipse-temurin-21 AS build

COPY . .


# Этап запуска

RUN mvn clean package -DskipTests

WORKDIR /app

COPY --from=build /app/target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

WORKDIR /app

COPY --from=build /app/target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
