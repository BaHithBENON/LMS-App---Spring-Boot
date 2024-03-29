# Generate Build
FROM maven:3-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Dockerize
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/library-management-system-0.0.1-SNAPSHOT.jar ./lmsapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lmsapp.jar"]