package com.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class MachineSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineScheduleId")
    private Integer machineScheduleId;

    @Column(name = "currentProductionStepId")
    private ProductionStep productionStep;

    @OneToMany(mappedBy = "machineSchedule")
    private List<ScheduledProdutionStep> scheduledProdutionSteps = new ArrayList<>();

    @OneToOne(mappedBy = "machineScheduleId")
    private Machine machine;

    public MachineSchedule() {
    }

    public MachineSchedule(Integer machineScheduleId, ProductionStep productionStep, List<ScheduledProdutionStep> scheduledProdutionSteps, Machine machine) {
        this.machineScheduleId = machineScheduleId;
        this.productionStep = productionStep;
        this.scheduledProdutionSteps = scheduledProdutionSteps;
        this.machine = machine;
    }

    public Integer getMachineScheduleId() {
        return machineScheduleId;
    }

    public void setMachineScheduleId(Integer machineScheduleId) {
        this.machineScheduleId = machineScheduleId;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(ProductionStep productionStep) {
        this.productionStep = productionStep;
    }

    public List<ScheduledProdutionStep> getScheduledProdutionSteps() {
        return scheduledProdutionSteps;
    }

    public void setScheduledProdutionSteps(List<ScheduledProdutionStep> scheduledProdutionSteps) {
        this.scheduledProdutionSteps = scheduledProdutionSteps;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "MachineSchedule{" +
                "machineScheduleId=" + machineScheduleId +
                ", productionStep=" + productionStep +
                ", scheduledProdutionSteps=" + scheduledProdutionSteps +
                ", machine=" + machine +
                '}';
    }
}
