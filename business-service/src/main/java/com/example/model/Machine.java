package com.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineId")
    private Long machineId;

    @OneToOne(mappedBy ="machine", cascade = CascadeType.ALL)
    private MachineSchedule machineSchedule;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    private List<ScheduledProductionStep> scheduledProductionSteps = new ArrayList<>();

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    private List<ProductionStep> productionSteps = new ArrayList<>();

    @Column(name="backLog")
    private String backLog;
    @Column(name="machineType")
    private String machineType;
    @Column(name="name")
    private String name;
    @Column(name="requiredWorkers")
    private String requiredWorkers;
    @Column(name="machineState")
    private String machineState;

    public Machine() {
    }

    public Machine(Long machineId, MachineSchedule machineSchedule, List<ScheduledProductionStep> scheduledProductionSteps,
                   String backLog, String machineType, String name, String requiredWorkers, String machineState) {
        this.machineId = machineId;
        this.machineSchedule = machineSchedule;
        this.scheduledProductionSteps = scheduledProductionSteps;
        this.backLog = backLog;
        this.machineType = machineType;
        this.name = name;
        this.requiredWorkers = requiredWorkers;
        this.machineState = machineState;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public MachineSchedule getMachineSchedule() {
        return machineSchedule;
    }

    public void setMachineSchedule(MachineSchedule machineSchedule) {
        this.machineSchedule = machineSchedule;
    }

    public List<ScheduledProductionStep> getScheduledProdutionSteps() {
        return scheduledProductionSteps;
    }

    public void setScheduledProdutionSteps(List<ScheduledProductionStep> scheduledProductionSteps) {
        this.scheduledProductionSteps = scheduledProductionSteps;
    }

    public String getBackLog() {
        return backLog;
    }

    public void setBackLog(String backLog) {
        this.backLog = backLog;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequiredWorkers() {
        return requiredWorkers;
    }

    public void setRequiredWorkers(String requiredWorkers) {
        this.requiredWorkers = requiredWorkers;
    }

    public String getMachineState() {
        return machineState;
    }

    public void setMachineState(String machineState) {
        this.machineState = machineState;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId=" + machineId +
                ", machineSchedule=" + machineSchedule +
                ", scheduledProdutionSteps=" + scheduledProductionSteps +
                ", backLog='" + backLog + '\'' +
                ", machineType='" + machineType + '\'' +
                ", name='" + name + '\'' +
                ", requiredWorkers='" + requiredWorkers + '\'' +
                ", machineState='" + machineState + '\'' +
                '}';
    }
}
