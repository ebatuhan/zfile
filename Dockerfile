FROM maven:3.9.7-eclipse-temurin-21

WORKDIR /app
COPY pom.xml .
COPY src ./src

CMD ["sh", "-c", "mvn clean spring-boot:run -Dspring-boot.run.profiles=dev"]
