package com.example.inventoryservice;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Adds initial material data when the application starts and logs it to the console.
 */
@Component
public class DataInitialiser implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    public DataInitialiser(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        if (inventoryRepository.count() == 0) {
            System.out.println("--------------------------------------------------");
            System.out.println("[Init] Setting default materials into inventory...");
            System.out.println("--------------------------------------------------");

            inventoryRepository.save(new Material(0, "Steel Sheet", 500));
            inventoryRepository.save(new Material(0, "Aluminum Rod", 300));
            inventoryRepository.save(new Material(0, "Copper Wire", 200));
            inventoryRepository.save(new Material(0, "Plastic Resin", 150));

            System.out.println("[Init] Default materials have been added.");
        } else {
            System.out.println("[Init] Existing materials found — skipping material.");
        }

        // Log current inventory after startup
        System.out.println("--------------------------------------------------");
        System.out.println("[Inventory] Current materials in stock:");
        inventoryRepository.findAll().forEach(material ->
                System.out.println("   • " + material.getName() + " → " + material.getQuantity() + " units")
        );
        System.out.println("--------------------------------------------------");
    }
}
