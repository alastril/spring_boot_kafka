package com.myboot.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {


    public ZonedDateTimeDeserializer() {
        this(null);
    }

    protected ZonedDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String date = jsonParser.getText();
        if(date == null || date.isBlank()) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault());
    }
}
