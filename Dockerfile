# --- build stage ---
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew --version
COPY . .
RUN ./gradlew bootJar

# --- run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10001
ENTRYPOINT ["java","-jar","app.jar"]
