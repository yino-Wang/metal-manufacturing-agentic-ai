package com.example.domain.model;

import jakarta.persistence.*;

import java.util.Date;

/*Material entity class representing a material in the inventory system.
* */
@Entity
@Table(name = "material")
public class Material {
    @Id
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "location")
    private String location;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "current_quantity")
    private int currentQuantity;

    @Column(name = "source_lead_time")
    @Temporal(TemporalType.DATE)
    private Date sourceLeadTime; // in days

    @Column(name = "max_spend_price")
    private float maxSpendPrice;

    @Column(name = "required_quantity")
    private int requiredQuantity;


    public void setId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getId() {
        return materialId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Date getSourceLeadTime() {
        return sourceLeadTime;
    }

    public void setSourceLeadTime(Date sourceLeadTime) {
        this.sourceLeadTime = sourceLeadTime;
    }

    public float getMaxSpendPrice() {
        return maxSpendPrice;
    }

    public void setMaxSpendPrice(float maxSpendPrice) {
        this.maxSpendPrice = maxSpendPrice;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(int requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }


}
