package com.myboot.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.kafka.converters.MessJsonMessageConverter;
import com.myboot.entity.MessageSimple;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile({"Consumer"})
public class KafkaConfigConsumer {

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * ContainerFactory bean for listener in @{@link com.myboot.kafka.KafkaConsumer}. This container factory replace default factory initiated by spring boot
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageSimple> containerFactory(ConsumerFactory<String, MessageSimple> consumerFactory, KafkaTemplate<String, MessageSimple> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, MessageSimple> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        //re init this for sending objects to kafka
        factory.setConsumerFactory(consumerFactory);
        //As default by SpringBoot KafkaTemplate work as String to String. This custom Kafka template help us reply by objects directly
        factory.setReplyTemplate(kafkaTemplate);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> containerFactoryBatch(ConsumerFactory<String, List<MessageSimple>> consumerFactory, KafkaTemplate<String, MessageSimple> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        //re init this for sending objects to kafka
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        //As default by SpringBoot KafkaTemplate work as String to String. This custom Kafka template help us reply by objects directly
        factory.setReplyTemplate(kafkaTemplate);

        //convert list of objects from kafka to list POJO
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(new MessJsonMessageConverter(objectMapper)));
        return factory;
    }

    /**
     * ConsumerFactory bean for converting string object to @{@link MessageSimple} object by Listener methods
     */
    @Bean
    public ConsumerFactory<String, List<MessageSimple>> consumerListMessageFactoryMessage() {
        /**
         * //if not using converters ( setBatchMessageConverter(...) ) we can convert list objects to list of POJO.
//        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Message.class);
//        JsonDeserializer<List<Message>> jsonDeserializer = new JsonDeserializer<>(type, true);
         */
        JsonDeserializer<List<MessageSimple>> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.getTypeMapper().addTrustedPackages("com.myboot.*");
        return new DefaultKafkaConsumerFactory<>(
                customProperties(), new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, MessageSimple> consumerMessageFactoryMessage() {
        return new DefaultKafkaConsumerFactory<>(
                customProperties(), new StringDeserializer(),
                new JsonDeserializer<>(MessageSimple.class));
    }


    public Map<String, Object> customProperties() {
        Map<String, Object> map = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        map.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        return map;
    }

}
