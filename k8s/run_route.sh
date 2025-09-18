start powershell.exe -NoExit -Command "kubectl port-forward pods/sb-kafka-hibernate-pod 8083:8083 --namespace=sb-kafka"
start powershell.exe -NoExit -Command "kubectl port-forward pods/sb-kafka-cons-pod 8082:8082 --namespace=sb-kafka"
start powershell.exe -NoExit -Command "kubectl port-forward pods/sb-kafka-pub-pod 8081:8081 --namespace=sb-kafka"
start powershell.exe -NoExit -Command "kubectl port-forward pods/mysql-pod 3306:3306 --namespace=sb-kafka"
