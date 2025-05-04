package com.jobee.systemconfiguration.infrastructure.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class UtilsConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.setTimeZone(TimeZone.getTimeZone("UIC"));
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        return mapper;
    }
}
