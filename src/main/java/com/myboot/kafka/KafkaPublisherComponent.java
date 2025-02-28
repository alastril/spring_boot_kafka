package com.myboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.MessageSimple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Profile({"Publisher"})
public class KafkaPublisherComponent {

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LogManager.getLogger(KafkaPublisherComponent.class);
    @Autowired
    private KafkaTemplate<String, MessageSimple> kafkaTemplateStringSending;
    @Autowired
    private KafkaTemplate<String, List<MessageSimple>> kafkaTemplateObjectSending;

    public void sendToKafkaObjectAsString(String topicName, MessageSimple message) {
        CompletableFuture<SendResult<String, MessageSimple>> future =
                kafkaTemplateStringSending.send(topicName, UUID.randomUUID().toString(), message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes single obj {}", result);
            } else {
                LOGGER.error(ex.getMessage());
            }
        });
    }

    public void sendToKafkaObjectAsStringBatch(String topicName, List<MessageSimple> messages) {
        CompletableFuture<SendResult<String, List<MessageSimple>>> futureObject =
                kafkaTemplateObjectSending.send(topicName, UUID.randomUUID().toString(), messages);
        futureObject.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes object from batch {}", result);
            } else {
                LOGGER.error(ex.getMessage());
            }
        });
    }

}
