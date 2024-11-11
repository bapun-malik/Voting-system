FROM maven:3.9.8-eclipse-temurin-21 AS build

COPY . .

RUN mvn clean package -DskipTests

WORKDIR /app
RUN mvn clean install -U

FROM openjdk:21
COPY --from=build /app/target/mal-randomizer-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]