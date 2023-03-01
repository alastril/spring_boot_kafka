package com.myboot.kafka;

import com.myboot.entity.Message;
import com.myboot.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumer.class);

    @KafkaListener(id="myKafkaListener",topics = {Constants.TOPIC_FOR_SENDING}, containerFactory = "containerFactory")
    @SendTo(Constants.REPLY_TOPIC_FOR_SENDING)
    public Message listen(Message data) {
       LOGGER.debug("Data received {}", data);
        data.setMessage("updated message");
       return data;
    }

    @KafkaListener(id="myKafkaListenerReplyRead",topics = {Constants.REPLY_TOPIC_FOR_SENDING})
    public void listenReplyRead(String data) {
        LOGGER.debug("Data received from  Reply_Topic {}", data);
    }

}
