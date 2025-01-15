FROM maven:3-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-oracle
COPY --from=build /target/fishmail-0.0.1-SNAPSHOT.jar fishmail.jar
ENTRYPOINT [ "java","-jar","fishmail.jar" ]