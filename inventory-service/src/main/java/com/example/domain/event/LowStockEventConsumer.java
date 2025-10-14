package com.example.service;

import com.example.domain.event.LowStockEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the "low-stock-topic" Kafka topic.
 * Whenever a low stock event is published, this class receives it and logs it.
 */
@Component
public class LowStockEventConsumer {

    /**
     * Kafka listener method that automatically triggers when a message
     * is published to the "low-stock-topic".
     *
     * @param record Kafka record containing the LowStockEvent data.
     */
    @KafkaListener(topics = "low-stock-topic", groupId = "inventory-group")
    public void consumeLowStockEvent(ConsumerRecord<String, LowStockEvent> record) {
        LowStockEvent event = record.value();

        if (event != null) {
            System.out.println("[Kafka Consumer] Received low-stock event:");
            System.out.println("   → Material ID: " + event.getMaterialId());
            System.out.println("   → Name: " + event.getName());
            System.out.println("   → Quantity: " + event.getQuantity());
            System.out.println("   → Message: " + event.getMessage());
        } else {
            System.out.println("Received null event from Kafka!");
        }
    }
}
