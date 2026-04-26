# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /home/gradle/src
# Copy toàn bộ dự án để đảm bảo có đủ file cấu hình wrapper
COPY . .
# Cấp quyền cho gradlew và build
RUN chmod +x gradlew && ./gradlew clean build --no-daemon

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy file .jar (lưu ý: kiểm tra tên file, nếu có version thì dùng *.jar)
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]