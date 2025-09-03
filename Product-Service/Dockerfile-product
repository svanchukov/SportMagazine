FROM eclipse-temurin:21.0.2_13-jdk-jammy

COPY target/Product-Service-0.0.1-SNAPSHOT.jar /app/product-service.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]