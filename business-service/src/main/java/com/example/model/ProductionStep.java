package com.example.model;

import com.example.model.valueObjects.Consumable;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class ProductionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productionStepId")
    private Long id;

    @Column(name="stepName")
    private String stepName;

    @Column(name="machineHours")
    private Integer machineHours;
    @Column(name="manHours")
    private Integer manHours;
    @ElementCollection
    private List<Consumable> reqConsumables;
    @ManyToOne
    @JoinColumn(name="requiredMachine")
    private Machine machine;
    @Column(name="reqMachineType")
    private String reqMachineType;
    //DO NOT NEED?????? MAKES IT EMBEDDABLE IF NOT HAVE
//    @Column(name="positionInMachineSchedule")
//    private Integer positionInMachineSchedule;
    @Column(name="dependentOn")
    private String dependentOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId")
    private Job job;


    public ProductionStep() {
    }

    public ProductionStep(Long id, String stepName, Integer machineHours, Integer manHours, List<Consumable> reqConsumables, Machine machine, String reqMachineType, String dependentOn, Job job) {
        this.id = id;
        this.stepName = stepName;
        this.machineHours = machineHours;
        this.manHours = manHours;
        this.reqConsumables = reqConsumables;
        this.machine = machine;
        this.reqMachineType = reqMachineType;
        this.dependentOn = dependentOn;
        this.job = job;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ProductionStep{" +
                "id=" + id +
                ", stepName='" + stepName + '\'' +
                ", machineHours=" + machineHours +
                ", manHours=" + manHours +
                ", reqConsumables=" + reqConsumables +
                ", machine=" + machine +
                ", reqMachineType='" + reqMachineType + '\'' +
                ", dependentOn='" + dependentOn + '\'' +
                ", job=" + job +
                '}';
    }
}
