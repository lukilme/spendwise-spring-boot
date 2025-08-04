FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl bash git maven

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src ./src

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.fork=false"]
