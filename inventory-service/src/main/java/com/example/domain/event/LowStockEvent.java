package com.example.domain.event;

public class LowStockEvent {

    private String materialName;
    private long quantity;
    private String message;

    public LowStockEvent() {}

    public LowStockEvent(String materialName, long quantity) {
        this.materialName = materialName;
        this.quantity = quantity;
        this.message = "[Low Stock Alert] " + materialName + " is low with only " + quantity + " units left!";
    }

    public String getMaterialName() {
        return materialName;
    }

    public long getQuantity() {
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
