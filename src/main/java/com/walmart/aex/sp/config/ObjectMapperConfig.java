package com.walmart.aex.sp.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
