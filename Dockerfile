# Step 1: Build the application using Maven and JDK 17
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy your Maven `pom.xml` and source code
COPY pom.xml .
COPY src ./src

# Build the app (skip tests with -DskipTests if argument is passed)
ARG SKIP_TESTS=false
RUN if [ "$SKIP_TESTS" = "true" ]; then mvn clean package -DskipTests; else mvn clean package; fi

# Step 2: Use JDK 17 to run the application
FROM openjdk:17-jdk-slim

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the JAR from the build stage (ensure the file name matches the one generated by Maven)
COPY --from=build /app/target/enigwed-0.0.1-SNAPSHOT.jar /app/enigwed-api.jar

# Expose the port (usually 8080 for Spring Boot)
EXPOSE 8080

# Run the app using the JDK 17 runtime
ENTRYPOINT ["java", "-jar", "/app/enigwed-api.jar"]