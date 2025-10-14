package com.example.controller;

import com.example.domain.model.Material;
import com.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * REST API for managing inventory.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMaterial(@RequestBody Material material) {
        Material saved = inventoryService.saveMaterial(material);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("material", saved);
        response.put("message", saved.isLowStock()
                ? "Added, but stock is below 500 (Low Stock Alert)"
                : "Material added successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable int id, @RequestParam int quantity) {
        Material updated = inventoryService.updateStock(id, quantity);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("material", updated);
        response.put("message", updated.isLowStock()
                ? "Stock updated, but below 500 (Low Stock Alert)"
                : "Stock updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Material>> listAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }
}
