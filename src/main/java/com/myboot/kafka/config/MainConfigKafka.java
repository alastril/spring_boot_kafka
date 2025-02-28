package com.myboot.kafka.config;

import com.myboot.entity.MessageSimple;
import com.myboot.util.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile({"Consumer","Publisher"})
public class MainConfigKafka {

    @Autowired
    KafkaAdmin kafkaAdmin;
    @Value("${kafka.partition.count:5}")
    int countPartition;
    @Value("${kafka.publisher.fetch.max.wait.ms:50}")
    int maxWaitMs;
    @Value("${kafka.publisher.fetch.max.bytes:16384}")
    int maxBytes;

    @Bean
    public NewTopic topicForSending() {
        return TopicBuilder.name(Constants.TOPIC_FOR_SENDING)
                .partitions(countPartition)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic topicForSendingBatch() {
        return TopicBuilder.name(Constants.TOPIC_FOR_SENDING_BATCH)
                .partitions(countPartition)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topicForReply() {
        return TopicBuilder.name(Constants.REPLY_TOPIC_FOR_SENDING)
                .partitions(countPartition)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topicForReplyList() {
        return TopicBuilder.name(Constants.REPLY_TOPIC_FOR_SENDING_LIST)
                .partitions(countPartition)
                .replicas(1)
                .compact()
                .build();
    }


    @Bean
    public CommonErrorHandler commonErrorHandler() {
        return new KafkaErrorHandler();
    }

    /**
     * Beans for Sending in Kafka
     */
    @Bean
    public KafkaTemplate<String, MessageSimple> kafkaTemplateMessage() {
        return getDefaultKafkaTemplate();
    }

    @Bean
    public KafkaTemplate<String, List<MessageSimple>> kafkaTemplateMessageList() {
        return getDefaultKafkaTemplate();
    }

    private <K,V>KafkaTemplate<K, V> getDefaultKafkaTemplate(){
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        senderPropsForStringTemplate(), (Serializer<K>) new DelegatingSerializer(), new JsonSerializer<>()));
    }

    private Map<String, Object> senderPropsForStringTemplate() {
        Map<String, Object> props = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, DelegatingSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.LINGER_MS_CONFIG, maxWaitMs);//how many wait ms until send all data in one batch
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, maxBytes);//batch size in bytes
        return props;
    }
    /** Beans for Sending in Kafka ===========> End */
}
