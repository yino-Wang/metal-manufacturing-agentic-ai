package com.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class ProductionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productionStepId")
    private String productionStepId;
    @Column(name="stepName")
    private String stepName;

    @Column(name="machineHours")
    private Integer machineHours;
    @Column(name="manHours")
    private Integer manHours;
    @OneToMany(mappedBy = "scheduleProductionStep")
    private List<Consumable> reqConsumables = new ArrayList<>();
    @Column(name="reqMachine")
    private Machine machine;
    @Column(name="reqMachineType")
    private String reqMachineType;
    @Column(name="positionInMachineSchedule")
    private Integer positionInMachineSchedule;
    @Column(name="dependentOn")
    private String dependentOn;
    @Column(name="partOfJob")
    private Job job;

    public ProductionStep() {
    }

    public ProductionStep(String stepName, Integer machineHours,
                          Integer manHours, List<Consumable> reqConsumables,
                          Machine machine, String reqMachineType, Integer positionInMachineSchedule,
                          String dependentOn, Job job) {
        this.stepName = stepName;
        this.machineHours = machineHours;
        this.manHours = manHours;
        this.reqConsumables = reqConsumables;
        this.machine = machine;
        this.reqMachineType = reqMachineType;
        this.positionInMachineSchedule = positionInMachineSchedule;
        this.dependentOn = dependentOn;
        this.job = job;
    }

    public String getProductionStepId() {
        return productionStepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getMachineHours() {
        return machineHours;
    }

    public void setMachineHours(Integer machineHours) {
        this.machineHours = machineHours;
    }

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public List<Consumable> getReqConsumables() {
        return reqConsumables;
    }

    public void setReqConsumables(List<Consumable> reqConsumables) {
        this.reqConsumables = reqConsumables;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public String getReqMachineType() {
        return reqMachineType;
    }

    public void setReqMachineType(String reqMachineType) {
        this.reqMachineType = reqMachineType;
    }

    public Integer getPositionInMachineSchedule() {
        return positionInMachineSchedule;
    }

    public void setPositionInMachineSchedule(Integer positionInMachineSchedule) {
        this.positionInMachineSchedule = positionInMachineSchedule;
    }

    public String getDependentOn() {
        return dependentOn;
    }

    public void setDependentOn(String dependentOn) {
        this.dependentOn = dependentOn;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
