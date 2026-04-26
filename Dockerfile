# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /home/gradle/src

# Copy toàn bộ dự án
COPY . .

# Cấp quyền cho gradlew và build
RUN chmod +x gradlew && ./gradlew clean build --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file .jar từ build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
