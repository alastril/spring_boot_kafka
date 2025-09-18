sh k8s/clean.sh
docker compose -f k8s/docker-compose-kafka-k8s.yml build
sh k8s/tag_push_apply.sh
$SHELL