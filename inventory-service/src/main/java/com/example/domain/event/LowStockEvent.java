package com.example.domain.event;

import com.example.domain.model.Material;

/**
 * Event published when stock falls below 500.
 */
public class LowStockEvent {
    private final int materialId;
    private final String name;
    private final int quantity;

    public LowStockEvent(Material material) {
        this.materialId = material.getId();
        this.name = material.getName();
        this.quantity = material.getQuantity();
    }

    public int getMaterialId() { return materialId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public String getMessage() {
        return "⚠ LOW STOCK ALERT: " + name +
                " has only " + quantity + " units left (below 500).";
    }
}
