package com.myboot;

import com.myboot.config.ConfigContainerLibDocker;
import com.myboot.config.ConfigTestComposeFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = ConfigTestComposeFile.class)
public abstract class MainTestClass {

    @Autowired
    DockerComposeContainer dockerComposeContainer;
    private static final Logger LOGGER = LogManager.getLogger(MainTestClass.class);

    @AfterAll
    public void destroy() throws Exception {
        LOGGER.debug("Stopping docker containers...");
        dockerComposeContainer.stop();
        LOGGER.debug("Docker containers stopped!");
    }
}
