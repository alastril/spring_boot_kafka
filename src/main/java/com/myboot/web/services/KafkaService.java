package com.myboot.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaPublisher;
import com.myboot.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("Publisher")
public class KafkaService {

    @Autowired
    KafkaPublisher kafkaPublisher;

    public void sendObjectToKafka(MessageSimple message) throws JsonProcessingException {
        kafkaPublisher.sendToKafkaObjectAsString(Constants.TOPIC_FOR_SENDING, message);
    }

    public void sendObjectToKafkaBatch(List<MessageSimple> message) throws JsonProcessingException {
        kafkaPublisher.sendToKafkaObjectAsStringBatch(Constants.TOPIC_FOR_SENDING, message);
    }
}
