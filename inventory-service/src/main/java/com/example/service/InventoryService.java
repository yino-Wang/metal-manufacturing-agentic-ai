package com.example.service;

import com.example.domain.event.LowStockEvent;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Handles business logic and publishes low stock events to Kafka.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Adds or updates a material.
     * Publishes a Kafka low-stock event if quantity < 500.
     */
    public Material saveMaterial(Material material) {
        Material saved = inventoryRepository.save(material);
        if (saved.isLowStock()) {
            LowStockEvent event = new LowStockEvent(saved);
            kafkaTemplate.send("low-stock-topic", event);
            System.out.println(event.getMessage());
        }
        return saved;
    }

    public List<Material> getAll() {
        return inventoryRepository.findAll();
    }

    public Material updateStock(int id, int newQty) {
        return inventoryRepository.findById(id).map(material -> {
            material.setQuantity(newQty);
            return saveMaterial(material);
        }).orElseThrow(() -> new RuntimeException("Material not found"));
    }
}
