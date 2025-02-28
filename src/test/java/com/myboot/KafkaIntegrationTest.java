package com.myboot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumerComponent;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

@ActiveProfiles(profiles = {"Publisher", "Consumer", "test"})
public class KafkaIntegrationTest extends MainTestClass {

    private static final Logger LOGGER = LogManager.getLogger(KafkaIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private KafkaConsumerComponent consumer;

    @Captor
    ArgumentCaptor<Message<MessageSimple>> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<List<Message<List<MessageSimple>>>> messageBatchArgumentCaptor;

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Value("${kafka.attempt.await.timeSec:3}")
    private int timePerAttemptSec;
    @Value("${kafka.test.awaitSec:5}")
    private int awaitSec;

    @BeforeAll
    public void waitingKafkaInit() throws Exception {
        awaitKafkaInit();
        LOGGER.info("kafka init was finished...");
    }

    public void awaitKafkaInit() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.putAll(kafkaAdmin.getConfigurationProperties());
        KafkaConsumer<String, String> consumer
                = new KafkaConsumer<>(props);
        boolean flag = true;
        ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();
        while (flag) {
            flag = threadPool.schedule(checkKafkaInit(consumer), timePerAttemptSec, TimeUnit.SECONDS).get();
        }
        consumer.close();
    }

    private Callable<Boolean> checkKafkaInit(KafkaConsumer<String, String> consumer) {
        return () -> {
            boolean flag = consumer.listTopics().keySet().stream().anyMatch("__consumer_offsets"::equals);//last topic in order for init
            if (flag) {
                LOGGER.info("topic - '__consumer_offsets' created! Continue...");
                return false;
            } else {
                LOGGER.error("'__consumer_offsets' not created. Another attempt...");
                return true;
            }
        };
    }

    @Test
    public void checkSendOneMessWithReply() throws Exception {
        MessageSimple message = new MessageSimple(1L, "order1");
        Awaitility.setDefaultTimeout(awaitSec, TimeUnit.SECONDS);

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(awaitSec)).untilAsserted(
                () -> {
                    Mockito.verify(consumer).listener(messageArgumentCaptor.capture());
                    Assert.isTrue(messageArgumentCaptor.getValue().getPayload().getBody().equals(message.getBody()),
                            "Message field must be equal objects");
                    Mockito.verify(consumer).listenReplyRead(messageArgumentCaptor.capture());
                    MessageSimple argMessageSimple = messageArgumentCaptor.getValue().getPayload();
                    Assert.isTrue(argMessageSimple.getId().equals(message.getId()) &&
                                    argMessageSimple.getBody().equals(message.getBody().toUpperCase()),
                            "Id should be equals!");
                });
    }

    @Test
    public void checkBatchListenerWithReply() throws Exception {
        Awaitility.setDefaultTimeout(awaitSec, TimeUnit.SECONDS);
        List<MessageSimple> messages = new ArrayList<>();
        messages.add(new MessageSimple(1L, "order1"));
        messages.add(new MessageSimple(2L, "order2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(awaitSec)).untilAsserted(
                () -> {
                    Mockito.verify(consumer).batchListener(messageBatchArgumentCaptor.capture());
                    List<MessageSimple> messageResult = messageBatchArgumentCaptor.getValue().
                            stream().findFirst().get().getPayload();
                    Assert.isTrue(messageResult.size() == 2, "Message list must be 2");
                    messageResult.forEach(element ->
                            Assert.isTrue(messages.contains(element), "element not equal =" + element.toString())
                    );
                    Mockito.verify(consumer).listenReplyListRead(messageBatchArgumentCaptor.capture());
                    messageResult = objectMapper.convertValue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload(),
                            new TypeReference<>() {
                            });

                    Assert.isTrue(messageResult.size() == 2, "Message in reply list must be 2");
                    Assert.isTrue(messageResult.stream().filter(m ->
                            messages.stream().filter(orgMess ->
                                            m.getId().equals(orgMess.getId()) &&
                                                    m.getBody().equals(orgMess.getBody().toUpperCase()))
                                    .count() == 1).count() == 2, "Objects must be modified");
                });
    }
}
