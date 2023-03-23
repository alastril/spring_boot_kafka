package com.myboot.kafka.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.MessageSimple;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;


@Component
@AllArgsConstructor
public class MessJsonMessageConverter extends JsonMessageConverter {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
        try {
            if(type.getTypeName().equals(String.class.getTypeName()))  {
                return objectMapper.writeValueAsString(record.value());
            } else {
                return objectMapper.readValue(objectMapper.writeValueAsString(record.value()), objectMapper.getTypeFactory().constructCollectionType(List.class, MessageSimple.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
