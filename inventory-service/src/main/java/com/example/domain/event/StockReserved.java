package com.example.domain.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Event class representing that stock has been reserved in the inventory system.
 */
@Entity
public class StockReserved{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="event_name")
    private final String eventName;

    @Column(name="material_id")
    private final Long materialId;

    @Column(name="reserved_quantity")
    private final int reservedQuantity;

    @Column(name="reserved_for_job_id")
    private final Long reservedForJobId;

    @Column(name="reservation_time")
    private final LocalDateTime reservationTime;

    @Column(name="location")
    private final String location;

    public StockReserved(Long materialId, int reservedQuantity, Long reservedForJobId, LocalDateTime reservationTime, String location, String eventName) {
        this.materialId = materialId;
        this.reservedQuantity = reservedQuantity;
        this.reservedForJobId = reservedForJobId;
        this.reservationTime = reservationTime;
        this.location = location;
        this.eventName = eventName;
    }


    public Long getMaterialId() {
        return materialId;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public Long getReservedForJobId() {
        return reservedForJobId;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public String getLocation() {
        return location;
    }

    public String getEventName() {
        return eventName;
    }

    public String setEventName(String eventName) {
        return this.eventName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "StockReserved{" +
                "id=" + id +
                ", eventName='" + eventName + '\'' +
                ", materialId=" + materialId +
                ", reservedQuantity=" + reservedQuantity +
                ", reservedForJobId=" + reservedForJobId +
                ", reservationTime=" + reservationTime +
                ", location='" + location + '\'' +
                '}';
    }

    /**
     * Overriding equals to ensure that two StockReserved objects are considered equal if they have the same values for all fields.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StockReserved that = (StockReserved) obj;
        return reservedQuantity == that.reservedQuantity &&
                materialId.equals(that.materialId) &&
                reservedForJobId.equals(that.reservedForJobId) &&
                reservationTime.equals(that.reservationTime) &&
                location.equals(that.location)&&
                eventName.equals(that.eventName);
    }

    /**
     * It's important to override hashCode when equals is overridden to maintain the general contract for the hashCode method,
     * which states that equal objects must have the same hash code.
     */
    @Override
    public int hashCode() {
        int result = materialId.hashCode(); //setting initial hash code based on materialId
        result = 31 * result + reservedQuantity; //31 is a prime number that helps in generating a unique hash code
        result = 31 * result + reservedForJobId.hashCode();
        result = 31 * result + reservationTime.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + eventName.hashCode();
        return result;
    }
}
