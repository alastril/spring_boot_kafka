package com.myboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaPublisher {

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LogManager.getLogger(KafkaPublisher.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendToKafka(String topicName,  Message message) throws JsonProcessingException {
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, objectMapper.writeValueAsString(message));
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.debug("Successes {}", result);
            }
            else {
                LOGGER.error(ex.getMessage());
            }
        });
    }
}
