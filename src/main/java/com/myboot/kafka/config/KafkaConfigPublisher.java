package com.myboot.kafka.config;

import com.myboot.entity.MessageSimple;
import com.myboot.util.Constants;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Configuration
public class KafkaConfigPublisher {

    @Autowired
    KafkaAdmin kafkaAdmin;

    @PostConstruct
    public void init(){

        kafkaAdmin.initialize();
    }
    @Bean
    @Profile({"Publisher"})
    public NewTopic topicForSending() {
        return TopicBuilder.name(Constants.TOPIC_FOR_SENDING)
                .partitions(5)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    @Profile({"Publisher"})
    public NewTopic topicForReply() {
        return TopicBuilder.name(Constants.REPLY_TOPIC_FOR_SENDING)
                .partitions(5)
                .replicas(1)
                .compact()
                .build();
    }


    /**
     * Beans for Sending in Kafka
     */
    @Bean
    public KafkaTemplate<String, MessageSimple> kafkaTemplateMessage() {
        return new KafkaTemplate<String, MessageSimple>(
                new DefaultKafkaProducerFactory<>(
                        kafkaAdmin.getConfigurationProperties(), new StringSerializer(), new JsonSerializer<>())){
            //When our method returning Iterable Object
            @Override
            public CompletableFuture<SendResult<String, MessageSimple>> send(String topic, MessageSimple data) {
                return super.send(topic, 1, UUID.randomUUID().toString(), data);
            }
            //When our method returning Single Object
            @Override
            public CompletableFuture<SendResult<String, MessageSimple>> send(Message<?> message) {
                return super.send(message.getHeaders().get(KafkaHeaders.TOPIC).toString(), 1, UUID.randomUUID().toString(), (MessageSimple) message.getPayload());
            }
        };
    }

    @Bean
    public KafkaTemplate<String, List<MessageSimple>> kafkaTemplateMessageList() {
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
