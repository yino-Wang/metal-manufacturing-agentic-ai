package com.example.domain.event;

public class MaterialAllocatedEvent {

    private String materialName;
    private long quantityAllocated;

    public MaterialAllocatedEvent() {}

    public MaterialAllocatedEvent(String materialName, long quantityAllocated) {
        this.materialName = materialName;
        this.quantityAllocated = quantityAllocated;
    }

    public String getMaterialName() {
        return materialName;
    }

    public long getQuantityAllocated() {
        return quantityAllocated;
    }
}
