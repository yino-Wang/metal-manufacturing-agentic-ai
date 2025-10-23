package com.example.interfaces.dto;

public class MaterialConsumedByName {

    private String name;
    private long quantity;  // Changed to long

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}