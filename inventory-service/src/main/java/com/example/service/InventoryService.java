package com.example.service;

import com.example.domain.event.LowStockEvent;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles business logic and publishes low stock events to Kafka.
 * Also seeds initial materials once the application is ready.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Seeds default materials into the inventory after application startup.
     * This runs only once, after JPA and the database are ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialMaterials() {
        if (inventoryRepository.count() == 0) {
            System.out.println("--------------------------------------------------");
            System.out.println("[Init] Adding default materials into inventory...");
            System.out.println("--------------------------------------------------");

            List<Material> materials = List.of(
                    new Material(0, "Steel", 150),
                    new Material(0, "Aluminium", 150),
                    new Material(0, "Copper", 150),
                    new Material(0, "Iron", 150)
            );

            inventoryRepository.saveAll(materials);
            System.out.println("[Init] Default materials added successfully!");
        } else {
            System.out.println("[Init] Materials already exist — skipping seed.");
        }
    }

    /**
     * Adds or updates a material.
     * Publishes a Kafka low-stock event if quantity < 100.
     */
    public Material saveMaterial(Material material) {
        Material saved = inventoryRepository.save(material);
        if (saved.isLowStock()) {
            LowStockEvent event = new LowStockEvent(saved);
            try {
                if (kafkaTemplate != null) {
                    kafkaTemplate.send("low-stock-topic", event);
                }
            } catch (Exception e) {
                System.out.println("[Warning] Kafka unavailable, skipping event send.");
            }
            System.out.println(event.getMessage());
        }
        return saved;
    }

    /**
     * Returns all materials currently in inventory.
     */
    public List<Material> getAll() {
        return inventoryRepository.findAll();
    }

    /**
     * Updates the stock quantity of an existing material.
     * If stock falls below threshold, automatically adds 200 units.
     */
    public Material updateStock(int id, int newQty) {
        return inventoryRepository.findById(id).map(material -> {
            material.setQuantity(newQty);

            // Auto-restock if below 100
            if (material.getQuantity() < 100) {
                System.out.println("[Auto Restock] " + material.getName() +
                        " below 100 units. Adding 200 more automatically.");
                material.setQuantity(material.getQuantity() + 200);
            }

            return saveMaterial(material);
        }).orElseThrow(() -> new RuntimeException("Material not found"));
    }
}
