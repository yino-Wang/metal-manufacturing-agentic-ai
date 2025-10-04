package com.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineId")
    private String machineId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machineScheduleId")
    private MachineSchedule machineScheduleId;

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

    public Machine(String machineId, String machineType, String name, String requiredWorkers, String machineState) {
        this.machineId = machineId;
        this.machineType = machineType;
        this.name = name;
        this.requiredWorkers = requiredWorkers;
        this.machineState = machineState;
    }

    //all attributes
    public Machine(String machineId, String backLog, String machineType, String name, String requiredWorkers, String machineState) {
        this.machineId = machineId;
        this.backLog = backLog;
        this.machineType = machineType;
        this.name = name;
        this.requiredWorkers = requiredWorkers;
        this.machineState = machineState;
    }

    public String getMachineId() {
        return machineId;
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

    public String getName() {
        return name;
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
                "machineId='" + machineId + '\'' +
                ", backLog='" + backLog + '\'' +
                ", machineType='" + machineType + '\'' +
                ", name='" + name + '\'' +
                ", requiredWorkers='" + requiredWorkers + '\'' +
                ", machineState='" + machineState + '\'' +
                '}';
    }
}
