1) Install kubernetes on docker desktop(windows 11):
2) 
useful commands:
docker builder prune - clean docker build cache
clean.sh - just clean k8s: pods, services, configmaps; clean **sb_boot_kafka** images from docker;
kubectl logs <pod_name> -c <int_container_name> -n sb-kafka - debug init container