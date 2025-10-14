package com.example;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application entry point.
 * Loads initial materials into H2 on startup.
 */
@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
        System.out.println("🚀 Inventory Service started with H2 + Kafka.");
    }

    @Bean
    CommandLineRunner preload(InventoryRepository repo) {
        return args -> {
            System.out.println("📦 Preloading initial stock into H2 database...");
            repo.save(new Material(101, "Steel", 600));
            repo.save(new Material(102, "Iron", 300));
            repo.save(new Material(103, "Copper", 1200));
            repo.save(new Material(104, "Aluminium", 450));
            System.out.println("✅ Initial materials loaded.");
        };
    }
}
