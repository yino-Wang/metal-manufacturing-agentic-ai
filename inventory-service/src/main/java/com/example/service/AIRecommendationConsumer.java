package com.example.service;

import com.example.domain.event.LowStockEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * When a LowStockEvent is consumed from Kafka,
 * this class triggers the AI to generate a recommendation.
 */
@Component
public class AIRecommendationConsumer {

    private final AIRecommendationService aiService;

    public AIRecommendationConsumer(AIRecommendationService aiService) {
        this.aiService = aiService;
    }

    @KafkaListener(topics = "low-stock-topic", groupId = "ai-recommendation-group")
    public void handleLowStock(ConsumerRecord<String, LowStockEvent> record) {
        LowStockEvent event = record.value();
        if (event == null) return;

        System.out.println("[AI Recommendation Consumer] Low stock detected for: " + event.getName());
        String response = aiService.getRecommendation(event.getName(), event.getQuantity(), 100);
        System.out.println("AI Suggestion → " + response);
    }
}
