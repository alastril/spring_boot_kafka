package com.myboot.config;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.MockAdminClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.util.Properties;

/**
 * Using docker-compose file, Init-n config-n
 * Another type config
 * @see TestConfigComposeLibContainer
 */
@TestConfiguration
public class ConfigTestComposeFile {

    private Logger logger = LogManager.getLogger(ConfigTestComposeFile.class);

    @Container
    public static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer( new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("zookeeper_1",2181)
            .withExposedService("mysql_1",3306)
            .withExposedService("kafka_1",9092);

    @Bean
    public AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", dockerComposeContainer.getServiceHost("kafka_1", 9092) + ":" + dockerComposeContainer.getServicePort("kafka_1", 9092) );
        properties.put("request.timeout.ms", 3000);
        properties.put("connections.max.idle.ms", 5000);
        return MockAdminClient.create(properties);
    }
    @PreDestroy
    public void stopContainer(){
        logger.debug("Stopping docker containers...");
        dockerComposeContainer.stop();
        logger.debug("Docker containers stopped!");
    }

    static {
        dockerComposeContainer.start();
    }
}
