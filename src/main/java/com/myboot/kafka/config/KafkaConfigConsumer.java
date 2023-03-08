package com.myboot.kafka.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.kafka.converters.MessJsonMessageConverter;
import com.myboot.entity.Message;
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
@Profile("Consumer")
public class KafkaConfigConsumer {

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Autowired
    ObjectMapper objectMapper;

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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<Message>> containerFactoryBatch(ConsumerFactory<String, List<Message>> consumerFactory, KafkaTemplate<String, Message> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, List<Message>> factory
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
     * ConsumerFactory bean for converting string object to @{@link Message} object by Listener methods
     */
    @Bean
    public ConsumerFactory<String, List<Message>> consumerListMessageFactoryMessage() {
        /**
         * //if not using converters ( setBatchMessageConverter(...) ) we can convert list objects to list of POJO.
//        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Message.class);
//        JsonDeserializer<List<Message>> jsonDeserializer = new JsonDeserializer<>(type, true);
         */
        JsonDeserializer<List<Message>> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.getTypeMapper().addTrustedPackages("com.myboot.*");
        return new DefaultKafkaConsumerFactory<>(
                customProperties(), new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, Message> consumerMessageFactoryMessage() {
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructParametricType(List.class, Message.class);
        return new DefaultKafkaConsumerFactory<>(
                customProperties(), new StringDeserializer(),
                new JsonDeserializer<>(Message.class));
    }


    public Map<String, Object> customProperties() {
        Map<String, Object> map = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        map.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        return map;
    }

}
