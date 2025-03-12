# Build Stage
FROM maven:3.8.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src src
# Build the application and skip tests to save time (remove -DskipTests if tests are needed)
RUN mvn clean package -DskipTests


# Run Stage
FROM openjdk:21-jdk-slim
WORKDIR /app
# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]