# Multi-stage build for Spring Boot backend

# Stage 1: Build stage using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

# Copy entire project source
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage using OpenJDK
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/league-management-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
  || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
