package com.myboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myboot.entity.Message;
import com.myboot.entity.Order;
import com.myboot.kafka.KafkaPublisher;
import com.myboot.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class Application {

    @Autowired
    KafkaPublisher kafkaPublisher;
    static Logger logger = LogManager.getLogger(Application.class);
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        logger.info("Application has been started!");

    }
    @Bean
    public void send() throws JsonProcessingException {
        kafkaPublisher.sendToKafka(Constants.TOPIC_FOR_SENDING, new Message("my_first_new_topic" + LocalDateTime.now(), new Message("inner", new Order(1,"name"))));
    }
}