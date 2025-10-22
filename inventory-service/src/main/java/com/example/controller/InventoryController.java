package com.example.controller;

import com.example.service.MaterialEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final MaterialEventPublisher materialEventPublisher;

    public InventoryController(MaterialEventPublisher materialEventPublisher) {
        this.materialEventPublisher = materialEventPublisher;
    }

    @PostMapping("/allocate")
    public String allocateMaterial(@RequestParam String materialName, @RequestParam long quantity) {
        // Publish a material allocated event
        materialEventPublisher.publishMaterialAllocatedEvent(materialName, quantity);
        return "Material allocation event published!";
    }
}
