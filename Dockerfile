FROM ubuntu:latest
LABEL authors="thanhld"

# Use official OpenJDK 17 image as base
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/centralized-authentication-provider-0.0.1-SNAPSHOT.jar auth-provider.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "auth-provider.jar"]
