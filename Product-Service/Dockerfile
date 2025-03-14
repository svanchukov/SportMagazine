FROM openjdk:17-jdk-alpine
COPY target/product_service.jar /app/product_service.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "product_service.jar"]
