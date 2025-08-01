package com.myboot.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.util.Map;

/**
 * Using docker-compose file, Init-n config-n
 * Another type config
 * @see ConfigContainerLibDocker
 */
public class ConfigTestComposeFile implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private Logger LOGGER = LogManager.getLogger(ConfigTestComposeFile.class);
    public static final Map<String,Integer> serviceMap =
            Map.of("mysql_1",3306, "kafka_1",9095);

    @Container
    public  DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer( new File("src/test/resources/docker-compose-test.yml"));

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        serviceMap.forEach((key, value) ->
                dockerComposeContainer.withExposedService(key, value, Wait.forListeningPort()));
        applicationContext.getBeanFactory().registerResolvableDependency(DockerComposeContainer.class, dockerComposeContainer);
        dockerComposeContainer.start();
    }
}
