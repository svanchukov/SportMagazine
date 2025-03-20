FROM eclipse-temurin:21.0.2_13-jdk-jammy

COPY target/admin-server-0.0.1-SNAPSHOT.jar /app/admin-server.jar

WORKDIR /app

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/admin-server.jar"]