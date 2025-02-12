package com.myboot.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.*;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;

/**
 * Using docker-compose file, Init-n config-n
 * Another type config
 * @see ConfigContainerLibDocker
 */
public class ConfigTestComposeFile implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private Logger LOGGER = LogManager.getLogger(ConfigTestComposeFile.class);

    @Container
    public  DockerComposeContainer dockerComposeContainer = new DockerComposeContainer( new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("zookeeper_1",2181, Wait.forListeningPort())
            .withExposedService("mysql_1",3306, Wait.forListeningPort())
            .withExposedService("kafka_1",9092, Wait.forListeningPort());

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerResolvableDependency(DockerComposeContainer.class, dockerComposeContainer);
        dockerComposeContainer.start();
    }
}
