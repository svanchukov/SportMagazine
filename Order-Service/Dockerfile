FROM eclipse-temurin:21.0.2_13-jdk-jammy

COPY target/Order-Service-0.0.1-SNAPSHOT.jar /app/order-service.jar

WORKDIR /app

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]