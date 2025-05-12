# Используем официальный образ OpenJDK
FROM eclipse-temurin:21-jdk-jammy

# Рабочая директория в контейнере
WORKDIR /app

# Копируем JAR-файл (предварительно собранный через Maven/Gradle)
COPY target/gather_club_back-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, который использует Spring Boot
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
