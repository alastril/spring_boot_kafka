FROM maven:3.9.0 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean compile -P docker assembly:single

FROM openjdk:19-jdk-alpine
COPY --from=build /home/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

# "-Dspring.profiles.active","Publisher"