package com.example.service;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import com.example.interfaces.events.transform.JobAddedToMachineEvent;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Kafka stream consumer that processes JobAddedToMachineEvent messages.
     * When a job arrives, it deducts material usage and prints the updated inventory.
     */
    @Bean
    public Consumer<KStream<String, JobAddedToMachineEvent>> process() {
        return inputStream -> inputStream.foreach((key, event) -> {

            String materialName = event.getMaterialName();
            int materialRequired = event.getMaterialRequired();

            System.out.println("--------------------------------------------------");
            System.out.println("[Job Received] Material: " + materialName + " | Amount required: " + materialRequired);

            inventoryRepository.findByName(materialName).ifPresentOrElse(material -> {
                int newQty = material.getQuantity() - materialRequired;

                if (newQty < 0) {
                    System.out.println("[Warning] Not enough " + materialName + " in stock! Job requires "
                            + materialRequired + ", but only " + material.getQuantity() + " available.");
                    newQty = 0;
                }

                material.setQuantity(newQty);
                inventoryRepository.save(material);

                System.out.println("[Inventory] Updated stock for " + materialName + ": " + newQty + " units remaining.");

                // ✅ Only restock when below 100
                if (newQty < 100) {
                    System.out.println("[Auto-Restock] " + materialName + " below threshold. Adding +100 units.");
                    material.setQuantity(newQty + 100);
                    inventoryRepository.save(material);
                    System.out.println("[Inventory] New stock level for " + materialName + ": " + material.getQuantity());
                }

                System.out.println("--------------------------------------------------");

            }, () -> System.out.println("[Error] Material not found in inventory: " + materialName));
        });
    }
}
