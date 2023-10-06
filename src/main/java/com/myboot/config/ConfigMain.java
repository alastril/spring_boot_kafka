package com.myboot.config;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@NoArgsConstructor
public class ConfigMain {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
