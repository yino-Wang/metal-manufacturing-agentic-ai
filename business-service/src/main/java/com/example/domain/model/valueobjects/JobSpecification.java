package com.example.domain.model.valueobjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Embeddable
public class JobSpecification {
    private static final long serialVersionUID = 1L;
    @Embedded
    @AttributeOverride(name = "unLocCode", column = @Column(name = "first_production_step"))
    private ProductionStep firstProductionStep;
    @Embedded
    @AttributeOverride(name = "unLocCode", column = @Column(name = "next_production_step"))
    private ProductionStep nextProductionStep;
    @Temporal(TemporalType.DATE)
    @Column(name = "submit_date")
    @NotNull
    private Date submitDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    @NotNull
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "projected_end_date")
    @NotNull
    private Date projectedEndDate;

    public JobSpecification() {
    }

    /**
     * @param firstProductionStep the first production step of the job
     * @param nextProductionStep the next production step for the job
     * @param submitDate date the job is due
     * @param startDate date the job is started
     * @param projectedEndDate projected end date of job
     */
    public JobSpecification(ProductionStep firstProductionStep, ProductionStep nextProductionStep,
                            Date submitDate, Date startDate, Date projectedEndDate) {
        this.firstProductionStep = firstProductionStep;
        this.nextProductionStep = nextProductionStep;
        this.submitDate = (Date) submitDate.clone();
        this.startDate = (Date) startDate.clone();
        this.projectedEndDate = (Date) projectedEndDate.clone();
    }

    public ProductionStep getFirstProductionStep() {
        return firstProductionStep;
    }

    public ProductionStep getNextProductionStep() {
        return nextProductionStep;
    }

    public Date getSubmitDate() {
        return new Date(submitDate.getTime());
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public Date getProjectedEndDate() {
        return new Date(projectedEndDate.getTime());
    }
}
