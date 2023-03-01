package com.myboot.kafka.config;

import com.myboot.entity.Message;
import com.myboot.util.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Autowired
    KafkaAdmin kafkaAdmin;
    @Autowired
            @Lazy
    KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public NewTopic topicForSending() {
        return new NewTopic(Constants.TOPIC_FOR_SENDING, 1, (short) 1);
    }
    @Bean
    public NewTopic topicForReply() {
        return new NewTopic(Constants.REPLY_TOPIC_FOR_SENDING, 1, (short) 1);
    }

    /**ContainerFactory bean for listener in @{@link com.myboot.kafka.KafkaConsumer}. This container factory replace default factory initiated by spring boot */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> containerFactory(ConsumerFactory<String, Message> consumerFactory, KafkaTemplate < String, Message > kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory< String, Message> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        //As default by SpringBoot KafkaTemplate work as String to String. This custom Kafka template help us reply by objects directly
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }
    /**ConsumerFactory bean for converting string object to @{@link Message} object by Listener methods*/
    @Bean
    public ConsumerFactory<String, Message> consumerMessageFactoryMessage()
    {
        return new DefaultKafkaConsumerFactory<>(
                kafkaAdmin.getConfigurationProperties(), new StringDeserializer(),
                new JsonDeserializer<>(Message.class));
    }

    @Bean
    public KafkaTemplate < String, Message > kafkaTemplateMessage() {
        return new KafkaTemplate <> (
                new DefaultKafkaProducerFactory<>(
                        kafkaAdmin.getConfigurationProperties(), new StringSerializer(), new JsonSerializer<>()));
    }
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }


    @Bean
    public KafkaTemplate <String, String> kafkaTemplateString() {
        return new KafkaTemplate <> (producerFactory());
    }
}
