FROM --platform=linux/amd64 openjdk:21
ARG JAR_FILE=target/*.jar
COPY ./target/todo-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
