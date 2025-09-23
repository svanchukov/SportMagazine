FROM openjdk:21-jdk-slim

WORKDIR /app

# Копируем jar-файл (он должен быть собран через mvn package)
COPY target/*.jar app.jar

# Экспонируем порт (Spring Boot сам знает, какой брать — через application.properties)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
