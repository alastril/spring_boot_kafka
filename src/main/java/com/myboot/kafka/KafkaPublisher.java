package com.myboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@Profile({"Publisher","Core"})
public class KafkaPublisher {

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LogManager.getLogger(KafkaPublisher.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateStringSending;
    @Autowired
    private KafkaTemplate<String, List<MessageSimple>> kafkaTemplateObjectSending;

    public void sendToKafkaObjectAsString(String topicName, MessageSimple message) throws JsonProcessingException {
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplateStringSending.send(topicName,0, UUID.randomUUID().toString(), objectMapper.writeValueAsString(message));
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes String {}", result);
            } else {
                LOGGER.error(ex.getMessage());
            }
        });
    }

    public void sendToKafkaObjectAsStringBatch(String topicName, List<MessageSimple> messages) throws JsonProcessingException {
        CompletableFuture<SendResult<String, List<MessageSimple>>> futureObject = kafkaTemplateObjectSending.send(topicName,4, UUID.randomUUID().toString(), messages);
        futureObject.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes Object {}", result);
            } else {
                LOGGER.error(ex.getMessage());
            }
        });
    }

    public void sendToKafkaObject(String topicName, List<MessageSimple> message) {
        CompletableFuture<SendResult<String, List<MessageSimple>>> future =
                kafkaTemplateObjectSending.send(topicName, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes {}", result);
            } else {
                LOGGER.error(ex.getMessage());
            }
        });
    }
}
