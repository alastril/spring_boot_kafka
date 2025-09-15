FROM maven:3.9.1 AS build
ARG MVN_REPOSITORY_LOCATION
COPY ../src /home/app/src
COPY ../docker_scripts/mvn_settings_docker.xml /root/.m2/mvn_settings_docker.xml
COPY ../repository /root/.m2/repository
COPY ../pom.xml /home/app
ENV DOCKER_HOST=tcp://host.docker.internal:2375
ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal
#you can remove test part if it local successful passed
RUN mvn -f /home/app/pom.xml clean compile -P docker assembly:single -s /root/.m2/mvn_settings_docker.xml

FROM openjdk:19-jdk-alpine
COPY --from=build /home/app/target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]