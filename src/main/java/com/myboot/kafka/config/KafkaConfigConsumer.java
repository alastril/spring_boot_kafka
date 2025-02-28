package com.myboot.kafka.config;

import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumerComponent;
import com.myboot.kafka.converters.JsonToListMessConverter;
import com.myboot.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.serializer.DelegatingDeserializer;
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
    CommonErrorHandler commonErrorHandler;

    @Value("${kafka.consumer.max.poll.records:10}")
    int maxPollRecord;
    @Value("${kafka.consumer.fetch.max.wait.ms:50}")
    int maxWaitMs;
    @Value("${kafka.consumer.fetch.max.bytes:16384}")
    int maxBytes;

    /**
     * ContainerFactory bean for listener in @{@link KafkaConsumerComponent}. This container factory replace default factory initiated by spring boot
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageSimple> containerFactory(ConsumerFactory<String, MessageSimple> consumerFactory, KafkaTemplate<String, MessageSimple> kafkaTemplate) {
        return getDefaultKafkaListenerFactory(consumerFactory, kafkaTemplate);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> containerFactoryBatch(ConsumerFactory<String, List<MessageSimple>> consumerFactory, KafkaTemplate<String, List<MessageSimple>> kafkaTemplate, JsonToListMessConverter jsonToListMessConverter) {
        ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> factory
                = getDefaultKafkaListenerFactory(consumerFactory, kafkaTemplate);
        factory.setBatchListener(true);
        //convert list of objects from kafka to list POJO
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(jsonToListMessConverter));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> containerFactoryBatchReply(ConsumerFactory<String, List<MessageSimple>> consumerFactory, KafkaTemplate<String, List<MessageSimple>> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, List<MessageSimple>> factory
                = getDefaultKafkaListenerFactory(consumerFactory, kafkaTemplate);
        factory.setBatchListener(true);
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
        return getDefaultKafkaConsumerFactory();
    }

    @Bean
    public ConsumerFactory<String, MessageSimple> consumerMessageFactoryMessage() {
        return getDefaultKafkaConsumerFactory();
    }

    private <K,V>ConcurrentKafkaListenerContainerFactory<K, V> getDefaultKafkaListenerFactory(ConsumerFactory<K, V> consumerFactory, KafkaTemplate<K, V> kafkaTemplate){
        ConcurrentKafkaListenerContainerFactory<K, V> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    private <K,V>ConsumerFactory<K, V> getDefaultKafkaConsumerFactory(){
        JsonDeserializer<V> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.getTypeMapper().addTrustedPackages(Constants.TRUSTED_PACKAGES);
        return new DefaultKafkaConsumerFactory<>(
                customProperties(), (Deserializer<K>) new DelegatingDeserializer(),
                jsonDeserializer);
    }

    public Map<String, Object> customProperties() {
        Map<String, Object> properties = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DelegatingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecord);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, maxWaitMs);
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, maxBytes);
        return properties;
    }

}
