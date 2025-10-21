package com.example.application;

import com.example.domain.commands.AddJobMaterialsCommand;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AllocateMaterialsCommandService {

    private final InventoryRepository inventoryRepository;

    public AllocateMaterialsCommandService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Automatically allocates materials when a new job event arrives.
     */
    @Transactional
    public void allocateMaterials(AddJobMaterialsCommand cmd) {
        System.out.println("[Service] Allocating materials for Job #" + cmd.getJobNumber());

        Material material = inventoryRepository.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(cmd.getMaterialName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Material not found: " + cmd.getMaterialName()));

        int newQty = material.getQuantity() - cmd.getMaterialAmount();
        material.setQuantity(newQty);

        if (newQty < 100) {
            System.out.println("[Auto-Restock] " + material.getName() + " below 100 units. Adding +200.");
            material.setQuantity(newQty + 200);
        }

        inventoryRepository.save(material);
        System.out.println("[Service] Updated stock for " + material.getName() +
                ": " + material.getQuantity() + " units remaining.");
    }
}
