package com.example.controller;

import com.example.domain.model.Material;
import com.example.infrastructure.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // ---------------------------------------------------------------
    // ✅ Add a new material
    // ---------------------------------------------------------------
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMaterial(@RequestBody Material material) {
        Material saved = inventoryRepository.save(material);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("material", saved);
        response.put("message", "Material added successfully (auto-restock applies if below 100).");

        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------------
    // ✅ Update material quantity (increase or decrease)
    // ---------------------------------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateMaterialQuantity(
            @PathVariable int id,
            @RequestParam int quantity) {

        return inventoryRepository.findById(id)
                .map(material -> {
                    material.setQuantity(material.getQuantity() + quantity);
                    inventoryRepository.save(material);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("material", material);
                    response.put("message", "Stock updated successfully (auto-restock applies if below 100).");

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("status", "error", "message", "Material not found.")));
    }

    // ---------------------------------------------------------------
    // ✅ Get all materials
    // ---------------------------------------------------------------
    @GetMapping("/list")
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = inventoryRepository.findAll();
        return ResponseEntity.ok(materials);
    }
}
