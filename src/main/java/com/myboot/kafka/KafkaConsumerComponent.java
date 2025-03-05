package com.myboot.kafka;

import com.myboot.entity.MessageSimple;
import com.myboot.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Profile({"Consumer"})
public class KafkaConsumerComponent {
    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumerComponent.class);

    @KafkaListener(id = "myKafkaListener", topics = {Constants.TOPIC_FOR_SENDING},
            containerFactory = "containerFactory")
    @SendTo(Constants.REPLY_TOPIC_FOR_SENDING)
    public Message<MessageSimple> listener(Message<MessageSimple> data) {
        LOGGER.debug("Single obj received listener {}", data);
        return getKafkaMessage(
                MessageSimple.builder()
                        .id(data.getPayload().getId())
                        .body(data.getPayload().getBody().toUpperCase()).build(), data.getHeaders());
    }

    @KafkaListener(id = "myKafkaListenerBatch", topics =
            {Constants.TOPIC_FOR_SENDING_BATCH},
            containerFactory = "containerFactoryBatch")
    @SendTo(Constants.REPLY_TOPIC_FOR_SENDING_LIST)
    public List<Message<List<MessageSimple>>> batchListener(List<Message<List<MessageSimple>>> data) {
        LOGGER.debug("Data batchListener {}", data);
        List<Message<List<MessageSimple>>> modifiedMyMess = new ArrayList<>();
        data.forEach(kafkaMess ->
                modifiedMyMess.add(
                        getKafkaMessage(
                                kafkaMess.getPayload().stream().map(element -> {
                                    LOGGER.debug("Data received body=>{}", element.getBody());
                                    return MessageSimple.builder()
                                            .id(element.getId())
                                            .body(element.getBody().toUpperCase()).build();
                                }).toList(), kafkaMess.getHeaders()))
        );
        LOGGER.debug("End batch work!");
        return modifiedMyMess;
    }

    @KafkaListener(id = "myKafkaListenerReplyRead", topics = Constants.REPLY_TOPIC_FOR_SENDING, containerFactory = "containerFactory")
    public void listenReplyRead(Message<MessageSimple> data) {
        LOGGER.debug("Data received from  Reply_Topic {}", data);
    }

    @KafkaListener(id = "myKafkaListenerReplyListRead", topics = Constants.REPLY_TOPIC_FOR_SENDING_LIST, containerFactory = "containerFactoryBatchReply")
    public void listenReplyListRead(List<Message<List<MessageSimple>>> data) {
        LOGGER.debug("Data received from Reply_Topic_List {}", data);
    }

    private <V> Message<V> getKafkaMessage(V data, MessageHeaders messageHeadersRequest) {
        Map<String, Object> mapHeaders = new HashMap<>();
        LOGGER.debug("messageHeadersRequest {}", messageHeadersRequest);
        mapHeaders.put(KafkaHeaders.KEY, Objects.requireNonNull(messageHeadersRequest.get(KafkaHeaders.RECEIVED_KEY)));
        MessageHeaders messageHeaders = new MessageHeaders(mapHeaders);
        return MessageBuilder.withPayload(data).copyHeaders(messageHeaders)
                .build();
    }
}
