package com.myboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.config.ConfigTestComposeFile;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles(profiles={"Publisher","Consumer","test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = ConfigTestComposeFile.class)
public class KafkaIntegrationTest {

    private static final Logger LOGGER = LogManager.getLogger(KafkaIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired(required = false)
    MySQLContainer mySQLContainer;

    @Autowired(required = false)
    KafkaContainer kafkaContainer;

    @Autowired(required = false)
    DockerComposeContainer dockerComposeContainer;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private KafkaConsumer consumer;

    @Captor
    ArgumentCaptor<MessageSimple> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<List<org.springframework.messaging.Message<List<MessageSimple>>>> messageBatchArgumentCaptor;

    @Captor
    ArgumentCaptor<List<String>> stringArgumentCaptor;

    @BeforeAll
    public void waitingKafkaInit() throws Exception {
        LOGGER.info("waiting 5sec...");
        Thread.sleep(5000);//value depends on system
    }

    @AfterAll
    public void destroy() throws Exception {
        LOGGER.debug("Stopping docker containers...");
        if (mySQLContainer != null) {
            mySQLContainer.stop();
        }
        if (kafkaContainer != null) {
            kafkaContainer.stop();
        }
        if(dockerComposeContainer != null) {
            dockerComposeContainer.stop();
        }
        LOGGER.debug("Docker containers stopped!");
    }

    @Test
   public void checkListenerZeroToOnePartitions() throws Exception {
        MessageSimple message = new MessageSimple(1L, "order");

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer).listener(messageArgumentCaptor.capture()));
        Assert.isTrue(!messageArgumentCaptor.getValue().getBody().equals(message.getBody()), "Message field must be Not equal objects");
        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture()));
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size()>=1, "Not equal objects");
    }

    @Test
    public void checkBatchListenerWithReply() throws Exception {
        List<MessageSimple> messages = new ArrayList<>();
        messages.add(new MessageSimple(1L, "order"));
        messages.add(new MessageSimple(2L, "order2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer).batchListener(messageBatchArgumentCaptor.capture()));
        Assert.isTrue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload().size() == 2, "Message list must be 2");
        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture()));
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size() >= 2, "Size must be 2");
    }
}
