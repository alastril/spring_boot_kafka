docker image tag confluentinc/cp-kafka:latest localhost:5000/kafka-controller_k:latest
docker image tag redis:latest localhost:5000/redis_k:latest
docker image tag mysql:latest localhost:5000/mysql_k:latest
docker image tag sb_kafka_pub:latest localhost:5000/sb_kafka_pub_k:latest
docker image tag sb_kafka_cons:latest localhost:5000/sb_kafka_cons_k:latest
docker image tag sb_kafka_hibernate:latest localhost:5000/sb_kafka_hibernate_k:latest
docker image tag sb_kafka_flyway:latest localhost:5000/sb_kafka_flyway_k:latest
docker image push localhost:5000/kafka-controller_k:latest
docker image push localhost:5000/redis_k:latest
docker image push localhost:5000/mysql_k:latest
docker image push localhost:5000/sb_kafka_pub_k:latest
docker image push localhost:5000/sb_kafka_cons_k:latest
docker image push localhost:5000/sb_kafka_hibernate_k:latest
docker image push localhost:5000/sb_kafka_flyway_k:latest
kubectl apply -f k8s/build/config-maps.yml
kubectl apply -f k8s/build/services.yml
kubectl apply -f k8s/build/pods.yml