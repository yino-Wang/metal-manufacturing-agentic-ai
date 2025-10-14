package com.example.infrastructure.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class GeminiConfig {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.google-ai-gemini.chat-model.model-name}")
    private String modelName;

    @Bean
    public ChatModel chatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.1)
                .maxOutputTokens(1000)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
