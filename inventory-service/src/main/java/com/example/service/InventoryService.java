package com.example.service;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles business logic for inventory.
 * Automatically restocks 200 units if stock < 100.
 * Adds initial materials when the service starts.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Adds the default materials at application startup if none exist.
     */
    @PostConstruct
    public void initialiseMaterials() {
        if (inventoryRepository.count() == 0) {
            System.out.println("--------------------------------------------------");
            System.out.println("[Init] Adding default materials into inventory...");
            System.out.println("--------------------------------------------------");

            inventoryRepository.saveAll(List.of(
                    new Material(1, "Steel", 150),
                    new Material(2, "Aluminium", 150),
                    new Material(3, "Copper", 150),
                    new Material(4, "Iron", 150)
            ));

            System.out.println("[Init] Default materials have been added.");
        } else {
            System.out.println("[Init] Existing materials found — skipping material.");
        }

        // Log current inventory
        System.out.println("--------------------------------------------------");
        System.out.println("[Inventory] Current materials in stock:");
        inventoryRepository.findAll().forEach(material ->
                System.out.println("   • " + material.getName() + " → " + material.getQuantity() + " units")
        );
        System.out.println("--------------------------------------------------");
    }

    /**
     * Adds or updates a material.
     * Automatically restocks +200 if below 100 units.
     */
    public Material saveMaterial(Material material) {
        Material saved = inventoryRepository.save(material);

        if (saved.getQuantity() < 100) {
            saved.setQuantity(saved.getQuantity() + 200);
            System.out.println("[Auto-Restock] Material '" + saved.getName()
                    + "' was below 100 units. Added +200 automatically.");
            saved = inventoryRepository.save(saved);
        }

        return saved;
    }

    /** Retrieves all materials. */
    public List<Material> getAll() {
        return inventoryRepository.findAll();
    }

    /**
     * Updates stock by ID, auto-restocks if below threshold.
     */
    public Material updateStock(int id, int newQty) {
        return inventoryRepository.findById(id).map(material -> {
            material.setQuantity(newQty);

            if (material.getQuantity() < 100) {
                material.setQuantity(material.getQuantity() + 200);
                System.out.println("[Auto-Restock] Material '" + material.getName()
                        + "' was below 100 units. Added +200 automatically.");
            }

            return inventoryRepository.save(material);
        }).orElseThrow(() -> new RuntimeException("Material not found"));
    }
}
