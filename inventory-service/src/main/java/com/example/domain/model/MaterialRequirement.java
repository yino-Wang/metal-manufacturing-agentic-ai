package com.example.domain.model;

import jakarta.persistence.*;
/*MaterialRequirement entity class representing a material requirement in the inventory system.
 * */

@Entity
@Table(name = "material_requirement")
public class MaterialRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialRequiredId;

    @Column(name = "required_location")
    private String requiredLocation;

    @Column(name = "required_material")
    private String requiredMaterial;

    @Column(name = "required_quantity")
    private int requiredQuantity;

    @Column(name = "for_scheduled_job")
    private Long forScheduledJobId;

    public MaterialRequirement(Long materialRequiredId, String requiredLocation, String requiredMaterial, int requiredQuantity, Long forScheduledJobId) {
        this.materialRequiredId = materialRequiredId;
        this.requiredLocation = requiredLocation;
        this.requiredMaterial = requiredMaterial;
        this.requiredQuantity = requiredQuantity;
        this.forScheduledJobId = forScheduledJobId;
    }

    public void setMaterialRequiredId(Long materialRequiredId) {
        this.materialRequiredId = materialRequiredId;
    }

    public Long getMaterialRequiredId() {
        return materialRequiredId;
    }

    public String getRequiredLocation() {
        return requiredLocation;
    }

    public void setRequiredLocation(String requiredLocation) {
        this.requiredLocation = requiredLocation;
    }

    public String getRequiredMaterial() {
        return requiredMaterial;
    }

    public void setRequiredMaterial(String requiredMaterial) {
        this.requiredMaterial = requiredMaterial;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(int requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public Long getForScheduledJobId() {
        return forScheduledJobId;
    }

    public void setForScheduledJobId(Long forScheduledJobId) {
        this.forScheduledJobId = forScheduledJobId;
    }
}
