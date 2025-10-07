package com.example.domain.model.valueobjects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.io.Serializable;

/**
 * A handling activity represents how and where a cargo can be handled, and can
 * be used to express predictions about what is expected to happen to a cargo in
 * the future.
 *
 */
@Embeddable
public class JobHandlingActivity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "next_expected_handling_event_type")
    private String type;
    @Embedded
    @AttributeOverride(name = "unLocCode", column = @Column(name = "next_expected_production_step"))
    private ProductionStep productionStep;
    @Column(name = "next_expected_required_materials")
    private RequiredMaterials requiredMaterials;

    public JobHandlingActivity() {
    }

    public JobHandlingActivity(String type, ProductionStep productionStep) {
        this.type = type;
        this.productionStep = productionStep;
    }

    public JobHandlingActivity(String type, ProductionStep productionStep,
                               RequiredMaterials requiredMaterials) {
        this.type = type;
        this.productionStep = productionStep;
        this.requiredMaterials = requiredMaterials;
    }

    public String getType() { return type; }

    public ProductionStep getLocation() {
        return productionStep;
    }

    public RequiredMaterials getVoyage() {
        return requiredMaterials;
    }


}
