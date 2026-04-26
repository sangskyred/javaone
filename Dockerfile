# Build stage
FROM gradle:7.5-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src
RUN gradle clean build --no-daemon

# Run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]