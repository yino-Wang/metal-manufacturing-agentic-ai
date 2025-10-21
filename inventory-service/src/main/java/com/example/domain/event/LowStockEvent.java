package com.example.domain.event;

import com.example.domain.model.Material;

public class LowStockEvent {

    private String materialName;
    private int quantity;
    private String message;

    public LowStockEvent() {}

    public LowStockEvent(Material material) {
        this.materialName = material.getName();
        this.quantity = material.getQuantity();
        this.message = "[Low Stock Alert] " + materialName + " is low with only " + quantity + " units left!";
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LowStockEvent{" +
                "materialName='" + materialName + '\'' +
                ", quantity=" + quantity +
                ", message='" + message + '\'' +
                '}';
    }
}
