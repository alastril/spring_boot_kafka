package com.myboot.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * one of the way docker-container init-n without docker-compose-file
 * Another type config
 * @see ConfigTestComposeFile
 */
@TestConfiguration
public class TestConfigComposeLibContainer {

    private static final Logger LOGGER = LogManager.getLogger(TestConfigComposeLibContainer.class);

    @Container
    public static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer( "latest");

    //Need for init properties because diff ports
    public static void overrideProperties() {
        LOGGER.info("datasource url: " + mySQLContainer.getJdbcUrl());
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
        System.setProperty("spring.datasource.password", "test");
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.url", mySQLContainer.getJdbcUrl());
    }
    static {
        mySQLContainer.start();
        kafkaContainer.start();
        overrideProperties();
    }
}
