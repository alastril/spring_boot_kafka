package com.myboot.kafka.config;

import com.myboot.entity.Message;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@Profile("Consumer")
public class KafkaConfigConsumer {

    @Autowired
    KafkaAdmin kafkaAdmin;

    /**
     * ContainerFactory bean for listener in @{@link com.myboot.kafka.KafkaConsumer}. This container factory replace default factory initiated by spring boot
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> containerFactory(ConsumerFactory<String, Message> consumerFactory, KafkaTemplate<String, Message> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        //re init this for sending objects to kafka
        factory.setConsumerFactory(consumerFactory);

        //As default by SpringBoot KafkaTemplate work as String to String. This custom Kafka template help us reply by objects directly
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    /**
     * ConsumerFactory bean for converting string object to @{@link Message} object by Listener methods
     */
    @Bean
    public ConsumerFactory<String, Message> consumerMessageFactoryMessage() {
        return new DefaultKafkaConsumerFactory<>(
                kafkaAdmin.getConfigurationProperties(), new StringDeserializer(),
                new JsonDeserializer<>(Message.class));
    }

}
