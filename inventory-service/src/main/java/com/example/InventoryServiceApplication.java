package com.example;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main entry point for the Inventory Service application.
 * Automatically seeds default materials on startup.
 */
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner seedData(InventoryRepository inventoryRepository) {
        return args -> {
            System.out.println("--------------------------------------------------");
            System.out.println("[Init] Adding default materials into inventory...");
            System.out.println("--------------------------------------------------");

            if (inventoryRepository.count() == 0) {
                inventoryRepository.save(new Material("Steel", 150));
                inventoryRepository.save(new Material("Aluminium", 150));
                inventoryRepository.save(new Material("Copper", 150));
                inventoryRepository.save(new Material("Iron", 150));
                System.out.println("[Init] Default materials added successfully!");
            } else {
                System.out.println("[Init] Materials already exist — skipping seeding.");
            }

            System.out.println("[Inventory] Current materials in stock:");
            inventoryRepository.findAll().forEach(material ->
                    System.out.println(material.getName() + " (ID: " + material.getId() + ") - "
                            + material.getQuantity() + " units")
            );
            System.out.println("--------------------------------------------------");
        };
    }
}
