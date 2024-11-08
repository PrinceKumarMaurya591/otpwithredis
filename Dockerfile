# Use official Maven image to build the application
FROM maven:3.8.5-openjdk-17 as build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY otp/pom.xml /app/
RUN mvn dependency:go-offline

# Copy the rest of the application files
COPY otp /app

# Build the application
RUN mvn clean install

# Use a slim JDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/otpwithredis.jar /app/otpwithredis.jar

# Expose the port on which the application runs
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/otpwithredis.jar"]
