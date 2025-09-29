package com.example.domain.event;

/**
 * Event class representing that low stock has been detected in the inventory system.
 */
public class LowStockDetected {
    private final Long materialId;
    private final String materialName;
    private final int currentQuantity;
    private final String location;
    private final int thresholdQuantity;

    public LowStockDetected(Long materialId, String materialName, int currentQuantity, String location, int thresholdQuantity) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.currentQuantity = currentQuantity;
        this.location = location;
        this.thresholdQuantity = thresholdQuantity;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public String getLocation() {
        return location;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    @Override
    public String toString() {
        return "LowStockDetected{" +
                "materialId=" + materialId +
                ", materialName='" + materialName + '\'' +
                ", currentQuantity=" + currentQuantity +
                ", location='" + location + '\'' +
                ", thresholdQuantity=" + thresholdQuantity +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LowStockDetected that = (LowStockDetected) obj;

        if (currentQuantity != that.currentQuantity) return false;
        if (thresholdQuantity != that.thresholdQuantity) return false;
        if (!materialId.equals(that.materialId)) return false;
        if (!materialName.equals(that.materialName)) return false;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        int result = materialId.hashCode();
        result = 31 * result + materialName.hashCode();
        result = 31 * result + currentQuantity;
        result = 31 * result + location.hashCode();
        result = 31 * result + thresholdQuantity;
        return result;
    }
}
