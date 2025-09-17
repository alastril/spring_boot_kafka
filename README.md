Application for testing SpringBoot with kafka.

1) install docker
2) set settings_mvn.xml to your Maven (or set in Idea maven config and repo location).
   after that run: `mvn clean compile install -P docker assembly:single`. 
   As a result you should have repository folder in project dir with libs( this repo use in k8s and jenkins image build)
3) run help tools mysql, redis, kafka, .etc.:
    - `docker compose -f docker_scripts/docker-compose-tools.yml up --build --force-recreate`
4) run in root dir project next command with maven build process:
    - `docker compose -f docker_scripts/docker-compose-kafka.yml build --build-arg MVN_REPOSITORY_LOCATION="../repository"`
    - `docker compose -f docker_scripts/docker-compose-kafka.yml up`
    OR run in root dir project next command WITHOUT maven build but should already have builded jar-file in target folder:
    - `docker compose -f docker_scripts/docker-compose-kafka-without-mvn.yml build --build-arg MVN_REPOSITORY_LOCATION="../repository"`
    - `docker compose -f docker_scripts/docker-compose-kafka-without-mvn.yml up`
      
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

run jenkins:
docker compose -f docker-compose-jenkins.yml up --build --force-recreate   
https://download.oracle.com/java/19/archive/jdk-19.0.2_linux-x64_bin.tar.gz

docker builder prune - clean docker build cache
JPA generate Tables, flyway init data
mklink /d mvn_repo\ c:\Users\Pasha\.m2\
Run flyway manual:
    for PS `mvn clean flyway:migrate "-Dflyway.configFiles=/flyway/flyway.conf"`
For docker image:
    `docker compose -f .\flyway\docker-compose-flyway.yml up  --build --force-recreate`
