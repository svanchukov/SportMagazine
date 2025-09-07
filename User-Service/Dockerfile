FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/User-Service-0.0.1-SNAPSHOT.jar.original app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]