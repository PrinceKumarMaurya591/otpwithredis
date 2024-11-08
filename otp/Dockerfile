# Use official openjdk as base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy pom.xml and download dependencies for faster builds (optional)
COPY otp/pom.xml /app/
RUN mvn dependency:go-offline

# Copy the whole application into the container
COPY otp /app

# Build the application
RUN mvn clean install

# Expose the port your application will run on (adjust as needed)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app/target/otpwithredis.jar"]
