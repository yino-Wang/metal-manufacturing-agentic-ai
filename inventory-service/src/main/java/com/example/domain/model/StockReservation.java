package com.example.domain.model;

/*StockReservation entity class representing a stock reservation in the inventory system.
avoids circular dependency with Material and MaterialRequirement.
 * */

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "stock_reservation")
public class StockReservation {
    @Id
    private Long id;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "reserved_quantity")
    private int reservedQuantity;

    @Column(name = "reserved_for_job_id")
    private Long reservedForJobId;

    @Column(name = "location")
    private String location;

    @Column(name = "reservation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime reservationTime;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Long getReservedForJobId() {
        return reservedForJobId;
    }

    public void setReservedForJobId(Long reservedForJobId) {
        this.reservedForJobId = reservedForJobId;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
