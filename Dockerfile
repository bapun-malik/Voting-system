FROM maven:3.9.8-eclipse-temurin-21 AS build

COPY . .

RUN mvn clean package -DskipTests



FROM openjdk:21
COPY --from=build /target/*.jar voting.jar


EXPOSE 8080

CMD ["java", "-jar", "app.jar"]