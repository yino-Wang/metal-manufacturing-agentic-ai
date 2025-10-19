package com.example.service;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles business logic for inventory.
 * Automatically restocks 200 units if stock < 100.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Adds or updates a material record.
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
