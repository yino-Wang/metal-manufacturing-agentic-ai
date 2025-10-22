package com.example.service;

import com.example.domain.event.MaterialAllocatedEvent;
import com.example.domain.event.LowStockEvent;
import com.example.infrastructure.repository.InventoryRepository;
import com.example.domain.model.Material;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StreamBridge streamBridge;

    public InventoryService(InventoryRepository inventoryRepository, StreamBridge streamBridge) {
        this.inventoryRepository = inventoryRepository;
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<MaterialAllocatedEvent> handleMaterialAllocation() {
        return event -> {
            String materialName = event.getMaterialName();
            long quantityAllocated = event.getQuantityAllocated();

            // Check if material exists in inventory
            inventoryRepository.findByName(materialName).ifPresentOrElse(material -> {
                long newQty = material.getQuantity() - quantityAllocated;

                if (newQty < 0) {
                    System.out.println("[Warning] Not enough " + materialName + " in stock!");
                    newQty = 0;
                }

                material.setQuantity(newQty);
                inventoryRepository.save(material);

                System.out.println("[Inventory] Updated stock for " + materialName + ": " + newQty + " units remaining.");

                // Check if stock is below the threshold and trigger auto-restocking
                if (newQty < 100) {
                    System.out.println("[Auto-Restock] " + materialName + " below threshold. Adding +100 units.");
                    material.setQuantity(newQty + 100);
                    inventoryRepository.save(material);

                    // Trigger Low Stock Event
                    streamBridge.send("lowStockChannel", new LowStockEvent(materialName, material.getQuantity()));
                }
            }, () -> System.out.println("[Error] Material not found in inventory: " + materialName));
        };
    }
}
