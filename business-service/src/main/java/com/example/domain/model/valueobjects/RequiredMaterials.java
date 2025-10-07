package com.example.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 *  Class representing the Cargo Voyage
 */
@Embeddable
public class RequiredMaterials {

    @Column(name = "required_materials", insertable = false, updatable = false)
    private String requiredMaterials;

    public RequiredMaterials(){}

    public RequiredMaterials(String reqMaterials){
        this.requiredMaterials = reqMaterials;
    }

    public String getRequiredMaterials(){return this.requiredMaterials;}

    public void setRequiredMaterials(String reqMaterials){this.requiredMaterials = reqMaterials;}
}
