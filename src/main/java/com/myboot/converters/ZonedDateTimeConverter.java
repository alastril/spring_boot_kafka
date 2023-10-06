package com.myboot.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(String source) {
        return LocalDate.parse(source,DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault());
    }
}
