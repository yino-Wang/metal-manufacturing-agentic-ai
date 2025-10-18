package com.example.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class GeminiConfig {

    // The langchain4j Spring Boot starter provides ChatModel auto-configuration.
    // Remove manual ChatModel bean to avoid relying on internal implementation classes.

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
