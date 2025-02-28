package com.myboot.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

public class KafkaErrorHandler implements CommonErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(KafkaErrorHandler.class);

    @Override
    public boolean handleOne(Exception exception, ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> consumer, MessageListenerContainer container) {
        handle(exception);
        return true;
    }

    @Override
    public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        handle(exception);
    }

    private void handle(Exception exception) {
        LOGGER.error("Exception thrown:", exception);
    }
}
