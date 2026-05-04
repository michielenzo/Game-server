FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
COPY ssl ./ssl

RUN mvn -B -ntp clean package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/ssl ./ssl
COPY --from=build /app/target/Game-server-1.0-SNAPSHOT.jar ./app.jar

CMD ["java", "-jar", "/app/app.jar"]
