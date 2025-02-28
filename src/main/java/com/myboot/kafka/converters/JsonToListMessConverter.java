package com.myboot.kafka.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.MessageSimple;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;


@Component
public class JsonToListMessConverter extends JsonMessageConverter {
    private static final Logger LOGGER = LogManager.getLogger(JsonToListMessConverter.class);

    ObjectMapper objectMapper;

    @Autowired
    public JsonToListMessConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> recordEntry, Type type) {
        try {
            List<LinkedHashMap<?,?>> resList = (List<LinkedHashMap<?,?>>) recordEntry.value();
            LOGGER.debug("recordEntry key {}, {}", recordEntry.topic(), recordEntry.headers());
            return resList.stream().map(obj ->
                    objectMapper.convertValue(obj, new TypeReference<MessageSimple>() {
                    })
            ).toList();
        } catch (Exception e) {
            LOGGER.error("Converting Exception: {}", e.getMessage());
            return null;
        }
    }
}
