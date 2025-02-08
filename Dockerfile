# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar DiscoveryServer.jar

# Expose eureka default port and run the application
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "DiscoveryServer.jar"]