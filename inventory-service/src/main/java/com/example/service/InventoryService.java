package com.example.service;

import com.example.domain.event.LowStockEvent;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import com.example.interfaces.events.transform.JobAddedToMachineEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Seeds default materials into the database after app startup.
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
            System.out.println("[Inventory] Current materials in stock:");

            inventoryRepository.findAll().forEach(m ->
                    System.out.println(m.getName() + " (ID: " + m.getId() + ") - " + m.getQuantity() + " units")
            );

            System.out.println("--------------------------------------------------");
        } else {
            System.out.println("[Init] Materials already exist — skipping seed.");
        }
    }

    /**
     * Adds or updates a material.
     * Sends Kafka event if low stock is detected.
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
     * Fetch all materials from inventory.
     */
    public List<Material> getAll() {
        return inventoryRepository.findAll();
    }

    /**
     * Update material quantity manually.
     * Auto-restocks if quantity is low.
     */
    public Material updateStock(int id, int newQty) {
        return inventoryRepository.findById(id).map(material -> {
            material.setQuantity(newQty);

            if (material.getQuantity() < 100) {
                System.out.println("[Auto Restock] " + material.getName() +
                        " below 100 units. Adding 200 more automatically.");
                material.setQuantity(material.getQuantity() + 200);
            }

            return saveMaterial(material);
        }).orElseThrow(() -> new RuntimeException("Material not found"));
    }

    /**
     * Stream listener for incoming job events.
     * Deducts the required materials and logs result.
     */
    @StreamListener("process-in-0")
    public void handleJobAddedToMachine(@Payload JobAddedToMachineEvent event) {
        System.out.println("--------------------------------------------------");
        System.out.println("[Job Received] Material required: " + event.getMaterialName());
        System.out.println("[Job Received] Quantity needed: " + event.getMaterialRequired());

        inventoryRepository.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(event.getMaterialName()))
                .findFirst()
                .ifPresentOrElse(material -> {
                    int before = material.getQuantity();
                    int after = before - event.getMaterialRequired();

                    material.setQuantity(after);
                    inventoryRepository.save(material);

                    System.out.println("[Inventory Update] " + material.getName() +
                            " reduced from " + before + " → " + after + " units.");

                    if (material.isLowStock()) {
                        System.out.println("[Warning] Low stock detected for " + material.getName() +
                                " (remaining: " + after + ")");
                        LowStockEvent lowStockEvent = new LowStockEvent(material);
                        try {
                            if (kafkaTemplate != null) {
                                kafkaTemplate.send("low-stock-topic", lowStockEvent);
                            }
                        } catch (Exception e) {
                            System.out.println("[Warning] Kafka unavailable, skipping low-stock event.");
                        }
                    }

                }, () -> System.out.println("[Warning] Material '" +
                        event.getMaterialName() + "' not found in inventory."));
        System.out.println("--------------------------------------------------");
    }
}
