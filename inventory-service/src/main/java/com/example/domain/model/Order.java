package com.example.domain.model;

/* Order entity class representing an order in the inventory system.
 * */

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long orderId;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "status")
    private String status; // e.g., "PENDING", "COMPLETED", "CANCELLED"

    @Column(name = "price")
    private double price;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_time")
    private Date createdTime;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
