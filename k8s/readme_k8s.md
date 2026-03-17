Install kubernetes on docker desktop(windows 11):
1) Enable in docker settings "Expose daemon on tcp://localhost:2375 without TLS"
2) Activate kubernetes on "docker settings": "Kubernetes" -> "Enable Kubernetes"
3) Install Helm, run in CMD `winget install Helm.Helm`
4) run in CMD `helm repo add kubernetes-dashboard https://kubernetes-retired.github.io/dashboard/`
5) run in CMD
   `helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard --create-namespace --namespace kubernetes-dashboard`
   if this command not work: try first this command - `helm repo update`, after that try again.
6) run in CMD `kubectl -n kubernetes-dashboard port-forward svc/kubernetes-dashboard-kong-proxy 8443:443`
7) Now Kubernetes Dashboard is available on https://localhost:8443/
8) run in CMD where our file `dashboard-adminuser.yaml`:
   `kubectl apply -f k8s/init/dashboard-adminuser.yml`
9) run in CMD where our file `dashboard-adminuser-bind-permission.yml`:
   `kubectl apply -f k8s/init/dashboard-adminuser-bind-permission.yml`
10) run in CMD and get token for login(1 hour live):
    `kubectl -n kubernetes-dashboard create token admin-user`
11) Copy generated token and enter on dashboard. Important: without spacebars as one row!
12) Now You can run script which clean pods in k8s,
    build images, tags and push them, apply pods, services, config-maps to
    k8s: [main_deploy_to_k8s.sh](main_deploy_to_k8s.sh) or do manual( run separately scripts from
    main_deploy_to_k8s.sh)
13) Run in CMD: `docker compose -f k8s/docker-compose-kafka-k8s.yml build --build --force-recreate`
14) do manual 15, 16, 17 cases or just run [tag_push_apply.sh](tag_push_apply.sh)
15) add tag to registry [tag_push_apply.sh](tag_push_apply.sh):
    - `docker image tag confluentinc/cp-kafka:latest localhost:5000/kafka-controller_k:7.8.7`
    - `docker image tag redis:latest localhost:5000/redis_k:latest`
    - `docker image tag mysql:latest localhost:5000/mysql_k:latest`
    - `docker image tag sb_kafka_pub:latest localhost:5000/sb_kafka_pub_k:latest`
    - `docker image tag sb_kafka_cons:latest localhost:5000/sb_kafka_cons_k:latest`
    - `docker image tag sb_kafka_hibernate:latest localhost:5000/sb_kafka_hibernate_k:latest`
    - `docker image tag sb_kafka_flyway:latest localhost:5000/sb_kafka_flyway_k:latest`
16) push image by tag to registry [tag_push_apply.sh](tag_push_apply.sh):
    - `docker image push localhost:5000/kafka-controller_k:7.8.7`
    - `docker image push localhost:5000/redis_k:latest`
    - `docker image push localhost:5000/mysql_k:latest`
    - `docker image push localhost:5000/sb_kafka_pub_k:latest`
    - `docker image push localhost:5000/sb_kafka_cons_k:latest`
    - `docker image push localhost:5000/sb_kafka_hibernate_k:latest`
    - `docker image push localhost:5000/sb_kafka_flyway_k:latest`
17) apply/deploy configs to K8s [tag_push_apply.sh](tag_push_apply.sh):
    - `kubectl apply -f k8s/build/config-maps.yml`
    - `kubectl apply -f k8s/build/services.yml`
    - `kubectl apply -f k8s/build/pods.yml`
18) execute run_route.sh or do manual 19,20,21,22(after pod rebuild need rerun script)
19) route port from kafka-hibernate pod to local network:
    `kubectl port-forward pods/sb-kafka-hibernate-pod 8083:8083 --namespace=sb-kafka`
20) route port from kafka-cons pod to local network:
    `kubectl port-forward pods/sb-kafka-cons-pod 8082:8082 --namespace=sb-kafka`
21) route port from kafka-pub pod to local network:
    `kubectl port-forward pods/sb-kafka-pub-pod 8081:8081 --namespace=sb-kafka`
22) route port from mysql pod to local network:
    `kubectl port-forward pods/mysql-pod 3306:3306 --namespace=sb-kafka`

useful commands:
docker builder prune - clean docker build cache
clean.sh - just clean k8s: pods, services, configmaps; clean **sb_boot_kafka** images from docker;
kubectl logs <pod_name> -c <int_container_name> -n sb-kafka - debug init container
kubectl logs sb-kafka-hibernate-pod -c wait-mysql -n sb-kafka