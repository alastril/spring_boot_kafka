package com.myboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.config.ConfigTestComposeFile;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaAdmin;
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
import java.util.*;

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

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Value("${kafka.attempt.await.timeMs:3000}")
    private int timePerAttemptMillis;
    @Value("${kafka.attempt.count:5}")
    private int attemptCount;

    @BeforeAll
    public void waitingKafkaInit() throws Exception {
        Properties props = new Properties();
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.putAll(kafkaAdmin.getConfigurationProperties());
        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer
                = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        final Map<String, List<PartitionInfo>> topics = new HashMap<>(consumer.listTopics());
        boolean  flag = true;
        int attempts = 0;
        while (flag) {
            flag = topics.keySet().stream().noneMatch("__consumer_offsets"::equals);//last topic in order for init
            if (flag) {
                topics.clear();
                topics.putAll(consumer.listTopics());
                LOGGER.error("topics="  + topics.keySet());
                //value depends on system
                Thread.sleep(timePerAttemptMillis);
                attempts++;
            } else {
                topics.values().forEach(p -> LOGGER.error("topics1="  + p.stream().count()));
                Thread.sleep(2000);
                topics.clear();
                topics.putAll(consumer.listTopics());
                topics.values().forEach(p -> LOGGER.error("topics2="  + p.stream().count()));
            }
            if(attempts > attemptCount) {
                LOGGER.error("Too long topic init -> '__consumer_offsets'. Continue...");
                break;
            }
        }
        consumer.close();

        LOGGER.info("kafka init was finished...");
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
        MessageSimple message = new MessageSimple(1L, "order1");

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
        messages.add(new MessageSimple(1L, "2order1"));
        messages.add(new MessageSimple(2L, "2order2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer).batchListener(messageBatchArgumentCaptor.capture()));
        Assert.isTrue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload().size() == 2, "Message list must be 2");
        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture()));
        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> Assert.isTrue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload().size() == 2, "Message list must be 2"));
    }
}
