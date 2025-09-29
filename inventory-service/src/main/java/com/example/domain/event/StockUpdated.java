package com.example.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event class representing that stock has been updated in the inventory system.
 */

public class StockUpdated implements Serializable {
    private final Long materialId;
    private final String materialName;
    private final int updatedQuantity;
    private final String location;
    private final String updateType; // e.g., "ADDED", "REMOVED", "RESERVED", "RELEASED"
    private final String createdBy;
    private final LocalDateTime reservationTime; // Time of the update
    private final String eventName;

    public StockUpdated(Long materialId, String materialName, int updatedQuantity, String location, String updateType, String createdBy, LocalDateTime reservationTime, String eventName) {
        this.eventName = eventName;
        this.materialId = materialId;
        this.materialName = materialName;
        this.updatedQuantity = updatedQuantity;
        this.location = location;
        this.updateType = updateType;
        this.createdBy = createdBy;
        this.reservationTime = reservationTime;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getUpdatedQuantity() {
        return updatedQuantity;
    }

    public String getLocation() {
        return location;
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String setEventName(String eventName) {
        return eventName;
    }

    @Override
    public String toString() {
        return "StockUpdated{" +
                "materialId=" + materialId +
                ", materialName='" + materialName + '\'' +
                ", updatedQuantity=" + updatedQuantity +
                ", location='" + location + '\'' +
                ", updateType='" + updateType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", reservationTime=" + reservationTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockUpdated that = (StockUpdated) o;
        return updatedQuantity == that.updatedQuantity &&
                materialId.equals(that.materialId) &&
                materialName.equals(that.materialName) &&
                location.equals(that.location) &&
                updateType.equals(that.updateType) &&
                createdBy.equals(that.createdBy) &&
                reservationTime.equals(that.reservationTime);
    }

    @Override
    public int hashCode() {
        int result = materialId.hashCode();
        result = 31 * result + materialName.hashCode();
        result = 31 * result + updatedQuantity;
        result = 31 * result + location.hashCode();
        result = 31 * result + updateType.hashCode();
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + reservationTime.hashCode();
        return result;
    }
}
