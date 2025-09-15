sh clean.sh
docker compose -f docker-compose-kafka-k8s.yml build
sh tag_push_apply.sh
$SHELL