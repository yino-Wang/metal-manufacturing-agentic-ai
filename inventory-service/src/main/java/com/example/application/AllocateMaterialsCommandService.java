package com.example.application;

import com.example.domain.commands.AddJobMaterialsCommand;
import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Application service that processes incoming AddJobMaterialsCommand
 * events from the business-service and adjusts the material inventory.
 *
 * If material quantity drops below 100 units after allocation,
 * it automatically restocks by +200 units.
 */
@Service
public class AllocateMaterialsCommandService {

    private final InventoryRepository inventoryRepository;

    /**
     * Constructor injection for repository dependency.
     */
    @Autowired
    public AllocateMaterialsCommandService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Handles a command to allocate materials for a given job.
     * Reduces the stock based on the job's material needs and
     * automatically restocks by 200 if the remaining stock is < 100.
     */
    @Transactional
    public void allocateMaterials(AddJobMaterialsCommand command) {
        String materialName = command.getMaterialName();
        int materialAmount = command.getMaterialAmount();

        // Find the material by name (case-insensitive match)
        Material material = inventoryRepository.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(materialName))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Material not found in inventory: " + materialName));

        // Deduct the required quantity for this job
        int updatedQuantity = material.getQuantity() - materialAmount;
        material.setQuantity(updatedQuantity);

        System.out.println("[Material Allocation] Job #" + command.getJobNumber() +
                " used " + materialAmount + " units of " + materialName +
                ". Remaining: " + updatedQuantity);

        // Auto-restock if below threshold
        if (material.getQuantity() < 100) {
            material.setQuantity(material.getQuantity() + 200);
            System.out.println("[Auto-Restock] " + materialName +
                    " dropped below 100 units. Added +200 automatically. New total: " +
                    material.getQuantity());
        }

        // Save updated stock
        inventoryRepository.save(material);
    }
}
