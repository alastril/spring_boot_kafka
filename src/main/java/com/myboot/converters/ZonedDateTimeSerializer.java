package com.myboot.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime> {
    private static final Logger LOGGER = LogManager.getLogger(ZonedDateTimeSerializer.class);

    //need for conversion
    public ZonedDateTimeSerializer() {
        this(null);
    }

    public ZonedDateTimeSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        if (zonedDateTime == null) {
            jsonGenerator.assignCurrentValue(null);
        } else {
            try {
                jsonGenerator.writeString(zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (IOException e) {
                LOGGER.debug("Error on serialize zonedDateTime = {}, {}", zonedDateTime, e.getMessage());
            }
        }

    }
}