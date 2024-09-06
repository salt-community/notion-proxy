FROM maven:3.8.8-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:21.0.3-alpine3.19

COPY --from=build /app/target/notionproxy-0.0.1-SNAPSHOT.jar notion-proxy.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","notion-proxy.jar"]