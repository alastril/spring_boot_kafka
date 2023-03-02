package com.myboot.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myboot.entity.Message;
import com.myboot.kafka.KafkaPublisher;
import com.myboot.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("Publisher")
public class KafkaService {

    @Autowired
    KafkaPublisher kafkaPublisher;

    public void sendObjectToKafka(Message message) throws JsonProcessingException {
        kafkaPublisher.sendToKafkaObjectAsString(Constants.TOPIC_FOR_SENDING, message);
    }

}
