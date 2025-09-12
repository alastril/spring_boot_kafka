kubectl delete pods --all -n sb-kafka
kubectl delete services --all -n sb-kafka
kubectl delete configmap --all -n sb-kafka
docker rmi localhost:5000/sb_kafka_pub_k localhost:5000/sb_kafka_hibernate_k localhost:5000/sb_kafka_cons_k sb_kafka_cons sb_kafka_hibernate sb_kafka_pub
