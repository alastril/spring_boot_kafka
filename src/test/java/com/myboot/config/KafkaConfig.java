package com.myboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.MainTestClass;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumerComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;


@EqualsAndHashCode(callSuper = true)
@Data
@ActiveProfiles(profiles = {"Publisher", "Consumer", "test"})
public class KafkaConfig extends MainTestClass {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConfig.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private KafkaConsumerComponent consumer;

    @Captor
    private ArgumentCaptor<Message<MessageSimple>> messageArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Message<List<MessageSimple>>>> messageBatchArgumentCaptor;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Value("${kafka.attempt.await.timeSec:10}")
    private int timePerAttemptSec;
    @Value("${kafka.test.awaitSec:5}")
    private int awaitSec;

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
}
