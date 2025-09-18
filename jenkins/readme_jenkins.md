1) Install kubernetes on docker desktop(windows 11), more details in k8s/readme_k8s.md:
    - [readme_k8s.md](../k8s/readme_k8s.md)
    - after installation k8s copy 'config'-file to jenkins folder(current folder where this readme-file) in project.
      Default path to config:
      Windows: c:\Users\<User_name>\.kube\config
      Linux: /home/<User_name>/.kube/config
2) Run command or just file [run_jenkins.sh](run_jenkins.sh):
    - `docker compose -f jenkins/docker-compose-jenkins.yml up`
    - it will take a few minutes, as result, you will see logs from containers
3) After prev. command when two services in container is starting you should be able to go http://localhost:8070/, it
   will ask password
4) Go to container 'jenkins' and find in logs this row(after this row will be password) :
   `Please use the following password to proceed to installation:`
5) Then use "Install suggested plugins". After complete, go to settings->plugins:
    - install plugin - Docker plugin, Pipeline: Stage View. Restart container
    - you also can install additional plugins if you need:
      1) Blue Ocean
      2) Docker Pipeline
6) Go to jenkins settings->Cloud->Add new cloud
   1) Name - any name
   2) type - docker
   2) Docker host URI - "tcp://host.docker.internal:2375"
   3) Apply checkboxes "Enabled", "Expose DOCKER_HOST"
   4) click "Test connection" and you will see "version and api version"
7) Add agent for builds, go to jenkins settings->nodes-> new node:
   - name - "test_agent" or look in [docker-compose-jenkins.yml](docker-compose-jenkins.yml) jenkins-agent->environment->JENKINS_AGENT_NAME value 
   - type - simple/Permanent agent
   - Number of executor processes >0 (better 2 or 3)
   - Remote root directory = /home/jenkins/agent 
   - labels = test_agent
   - Launch method = Launch the agent by connecting it to the controller
   - another settings as default
   - click on created agent and copy ssh key and paste in
     [docker-compose-jenkins.yml](docker-compose-jenkins.yml) jenkins-agent->environment->JENKINS_SECRET
   - stop jenkins containers, then run [_jenkins.sh](run_jenkins.sh)
8) Go to jenkins settings->Tools and add jdk, maven:
   1) JDK name = "jdk19"
       - click automated installation -> get from *.zip/*.tar.gz
       - set URL for download = https://download.oracle.com/java/19/archive/jdk-19.0.2_linux-x64_bin.tar.gz
       - subdirectory for archive = "jdk-19.0.2"
   2) Maven, name = "mvn391"
      - click automated installation -> "3.9.1" (another version may not build jar-file properly)
9) Go to Dashboard jenkins->Create Item:
   - set name and choose Pipeline
   - add script from jenkins/Jenkinsfile [Jenkinsfile](Jenkinsfile)
   - now you can run pipeline, as result you will get deployed app to k8s

useful commands:
docker builder prune - clean docker build cache
clean.sh - just clean k8s: pods, services, configmaps; clean **sb_boot_kafka** images from docker;
kubectl logs <pod_name> -c <int_container_name> -n sb-kafka - debug init container