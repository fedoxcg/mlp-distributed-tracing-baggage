FROM maven:3.9.4-eclipse-temurin-17 AS build
LABEL maintainer="fedegm3@gmail.com"

# Set the working directory
WORKDIR /home/app

# Copy the generated artifact to the container
COPY src /home/app/src
COPY pom.xml /home/app/pom.xml

ENV PROJECT_VERSION=1.1.1

RUN mvn clean package

FROM openjdk:17-jdk-slim

ENV JAEGER_ENDPOINT="http://127.0.0.1:14268/api/traces"
ENV JAEGER_SAMPLER_TYPE=const
ENV JAEGER_SAMPLER_PARAM=1
ENV JAEGER_REPORTER_LOG_SPANS=true
ENV PROJECT_VERSION=1.1.1

EXPOSE 8080
COPY --from=build /home/app/target/eshop-inventory-${PROJECT_VERSION}.jar /home/app/eshop-inventory.jar
ENTRYPOINT ["java", "-jar", "/home/app/eshop-inventory.jar"]