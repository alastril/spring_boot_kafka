package com.myboot.kafka.config;

import com.myboot.entity.Message;
import com.myboot.util.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfigPublisher {

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Bean
    @Profile("Publisher")
    public NewTopic topicForSending() {
        return TopicBuilder.name(Constants.TOPIC_FOR_SENDING)
                .partitions(5)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    @Profile("Publisher")
    public NewTopic topicForReply() {
        return TopicBuilder.name(Constants.REPLY_TOPIC_FOR_SENDING)
                .partitions(5)
                .replicas(1)
                .build();
    }


    /**
     * Beans for Sending in Kafka
     */
    @Bean
    public KafkaTemplate<String, Message> kafkaTemplateMessage() {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        kafkaAdmin.getConfigurationProperties(), new StringSerializer(), new JsonSerializer<>()));
    }

    @Bean
    public KafkaTemplate<String, List<Message>> kafkaTemplateMessageList() {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        kafkaAdmin.getConfigurationProperties(), new StringSerializer(), new JsonSerializer<>()));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(senderPropsForStringTemplate()));
    }

    private Map<String, Object> senderPropsForStringTemplate() {
        Map<String, Object> props = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    /** Beans for Sending in Kafka ===========> End */
}
