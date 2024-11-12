# Use an openjdk base image
FROM openjdk:17-jdk-slim

# Copy the JAR file into the container
COPY target/enigwed-0.0.1-SNAPSHOT.jar /app.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
