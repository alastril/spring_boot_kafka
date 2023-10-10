package com.myboot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;

/**
 * Using docker-compose file, Init-n config-n
 * Another type config
 * @see TestConfigComposeLibContainer
 */
@TestConfiguration
public class ConfigTestComposeFile {

    @Container
    public static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer( new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("zookeeper_1",2181)
            .withExposedService("mysql_1",3306)
            .withExposedService("kafka_1",9092);

    static {
        dockerComposeContainer.start();
    }
}
