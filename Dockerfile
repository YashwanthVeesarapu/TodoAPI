FROM maven:3.8.1-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn -f pom.xml clean package

FROM --platform=linux/amd64 openjdk:21
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ./target/todo-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
