package com.myboot.kafka;

import com.myboot.entity.MessageSimple;
import com.myboot.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Profile({"Consumer","Core"})
public class KafkaConsumer {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumer.class);

    @KafkaListener(id = "myKafkaListener", topicPartitions = {@TopicPartition(topic =  Constants.TOPIC_FOR_SENDING, partitions = {"0","1"})}, containerFactory = "containerFactory")
    @SendTo(Constants.REPLY_TOPIC_FOR_SENDING)
    public MessageSimple listener(MessageSimple data) {
        LOGGER.debug("Data received {}", data);
        data.setBody("updated message");
        return data;
    }
    @KafkaListener(id = "myKafkaListenerBatch", topicPartitions =
            {@TopicPartition(topic =  Constants.TOPIC_FOR_SENDING, partitionOffsets = {@PartitionOffset(partition = "2-4", initialOffset = "10")})},
            containerFactory = "containerFactoryBatch")
    @SendTo(Constants.REPLY_TOPIC_FOR_SENDING)
    public List<MessageSimple> batchListener(List<org.springframework.messaging.Message<List<MessageSimple>>> data) {
        ArrayList<MessageSimple> modifiedMyMess = new ArrayList<>();
        data.forEach(
                kafkaMess -> kafkaMess.getPayload().forEach(
                        myMess -> {
                            LOGGER.debug("Data received debug data=>{},\n payload=>{}", kafkaMess.getHeaders().toString(), myMess);
                            myMess.setBody("updated message from partition " + kafkaMess.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION));
                            modifiedMyMess.add(myMess);
                        }));

        LOGGER.debug("End batch work!");
        return modifiedMyMess;
    }

    @KafkaListener(id = "myKafkaListenerReplyRead", topics = Constants.REPLY_TOPIC_FOR_SENDING, containerFactory = "containerFactoryBatch")
    public void listenReplyRead(List<String> data) {
        LOGGER.debug("Data received from  Reply_Topic {}", data);
    }

}
