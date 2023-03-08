package com.myboot.kafka.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Component
@AllArgsConstructor
public class MessJsonMessageConverter extends JsonMessageConverter {

    ObjectMapper objectMapper;

    @Autowired
    public void MessagingMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
        try {
            if(type.getTypeName().equals(String.class.getTypeName()))  {
                return objectMapper.writeValueAsString(record.value());
            } else {
                return objectMapper.readValue(objectMapper.writeValueAsString(record.value()), objectMapper.getTypeFactory().constructCollectionType(List.class, com.myboot.entity.Message.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
