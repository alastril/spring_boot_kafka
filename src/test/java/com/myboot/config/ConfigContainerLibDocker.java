package com.myboot.config;


import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;

/**
 * one of the way docker-container init-n without docker-compose-file
 * Another type config
 * @see ConfigTestComposeFile
 */
@Data
public class ConfigContainerLibDocker implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LogManager.getLogger(ConfigContainerLibDocker.class);

    public MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    public KafkaContainer kafkaContainer = new KafkaContainer( "latest");

    //Need for init properties because diff ports
    public void overrideProperties() {
        LOGGER.info("datasource url: " + mySQLContainer.getJdbcUrl());
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
        System.setProperty("spring.datasource.password", "test");
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.url", mySQLContainer.getJdbcUrl()+"?useTimezone=true&serverTimezone=UTC");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerResolvableDependency(MySQLContainer.class, mySQLContainer);
        applicationContext.getBeanFactory().registerResolvableDependency(KafkaContainer.class, kafkaContainer);
        mySQLContainer.withConfigurationOverride("/");
        mySQLContainer.start();
        kafkaContainer.start();
        overrideProperties();
    }
}
