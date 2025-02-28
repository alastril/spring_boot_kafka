package com.myboot.services;

import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaPublisherComponent;
import com.myboot.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"Publisher"})
public class KafkaService {

    @Autowired
    KafkaPublisherComponent kafkaPublisherComponent;

    public void sendObjectToKafka(MessageSimple message) {
        kafkaPublisherComponent.sendToKafkaObjectAsString(Constants.TOPIC_FOR_SENDING, message);
    }

    public void sendObjectToKafkaBatch(List<MessageSimple> message) {
        kafkaPublisherComponent.sendToKafkaObjectAsStringBatch(Constants.TOPIC_FOR_SENDING_BATCH, message);
    }
}
