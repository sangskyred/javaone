# Build stage
# Sử dụng OpenJDK 25 cho quá trình biên dịch
FROM openjdk:25-jdk-slim AS build
WORKDIR /home/gradle/src

# Copy toàn bộ dự án
COPY . .

# Cấp quyền cho gradlew và build
RUN chmod +x gradlew && ./gradlew clean build --no-daemon

# Run stage
# Sử dụng JRE 25 để chạy ứng dụng (dùng bản slim để giảm dung lượng image)
FROM openjdk:25-jdk-slim 
WORKDIR /app

# Copy file .jar từ build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
