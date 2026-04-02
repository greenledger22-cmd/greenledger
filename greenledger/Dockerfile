# Use a lightweight JDK image as the base
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /deployment

# Copy the built JAR file from your local target/ or build/libs directory into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port on which the Spring Boot application runs (default is 8080)
EXPOSE 8080

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
