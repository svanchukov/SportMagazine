# Используем официальный образ OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine

# Указываем рабочую директорию
WORKDIR /app

# Копируем билдированный JAR-файл в контейнер
COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт (можно заменить, если порт другой в application.yml)
EXPOSE 8087

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
