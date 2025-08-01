Application for testing SpringBoot with kafka.

1) install docker

2) run in root dir project next command with maven build:
    - `docker compose -f docker-compose-kafka.yml up`
    OR run in root dir project next command WITHOUT maven build:
    - `docker compose -f docker-compose-kafka-without-mvn.yml up`

Optional, build jar with all dependencies(default build not working in docker, this need before running command "WITHOUT maven build"):
`mvn clean compile -P docker assembly:single`

first running local (application.properties):
spring.jpa.hibernate.ddl-auto=create or create-drop
another or with out DB changes:
spring.jpa.hibernate.ddl-auto=validate

Run Publisher: with VM option -Dspring.profiles.active=Publisher
Run Consumer: with VM option -Dspring.profiles.active=Consumer

1) Check code quality in sonar(sonar service must be already started on http://localhost:9000/):
    1) Create manually project with brunch you want
    2) generate login token(http://localhost:9000/admin/users , 'Tokens' field) and use in -Dsonar.login(next point): example token - squ_b5595ce778fd62616c147565a42e92d5a0d40319
    3) Examples : `mvn clean verify sonar:sonar -Pcoverage -Dsonar.projectKey=spring_kafka -Dsonar.host.url=http://localhost:9000 -Dsonar.login=squ_b5595ce778fd62616c147565a42e92d5a0d40319`
       mvn clean verify sonar:sonar -Pcoverage -Dsonar.projectKey=spring_kafka -Dsonar.host.url=http://localhost:9000 -Dsonar.login=squ_b5595ce778fd62616c147565a42e92d5a0d40319

run in cmd(useful for testing before docker):
java -jar spring_boot_kafka-1.0-SNAPSHOT-jar-with-dependencies.jar --spring.profiles.active=Hibernate,Core
