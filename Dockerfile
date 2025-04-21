FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src/ src/

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]