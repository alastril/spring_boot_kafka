package com.myboot.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime> {

    public ZonedDateTimeSerializer() {
        this(null);
    }

    public ZonedDateTimeSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(zonedDateTime == null) {
            jsonGenerator.assignCurrentValue(null);
        }
        jsonGenerator.writeString(zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}