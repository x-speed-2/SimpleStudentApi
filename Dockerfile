# Start with a base image with Java
FROM openjdk:17-jdk-slim

# Add a volume for logs (optional)
VOLUME /tmp

# Copy the application JAR
ARG JAR_FILE=target/SimpleStudentApi-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
