package com.example.application;

import com.example.domain.commands.AddJobMaterialsCommand;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        System.out.println("[Service] Allocating materials for Job...");

        Material material = inventoryRepository.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(cmd.getMaterialName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Material not found: " + cmd.getMaterialName()));

        long newQty = material.getQuantity() - cmd.getMaterialAmount();
        material.setQuantity(newQty);

        if (newQty < 100) {
            System.out.println("[Auto-Restock] " + material.getName() + " below threshold. Adding +100 units.");
            material.setQuantity(newQty + 100);
        }

        inventoryRepository.save(material);

        System.out.println("[Service] Updated stock for " + material.getName() +
                ": " + material.getQuantity() + " units remaining.");
    }
}
