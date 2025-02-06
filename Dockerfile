# Stage 1L Build stage with authentication for private github repo
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar NotificationService.jar

# Run application
ENTRYPOINT ["java", "-jar", "NotificationService.jar"]