package com.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineId")
    private String machineId;

    @OneToOne
    @JoinColumn(name = "machineScheduleFk")
    private MachineSchedule machineSchedule;

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

    public Machine(String machineId, MachineSchedule machineSchedule, String backLog, String machineType, String name, String requiredWorkers, String machineState) {
        this.machineId = machineId;
        this.machineSchedule = machineSchedule;
        this.backLog = backLog;
        this.machineType = machineType;
        this.name = name;
        this.requiredWorkers = requiredWorkers;
        this.machineState = machineState;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public MachineSchedule getMachineSchedule() {
        return machineSchedule;
    }

    public void setMachineSchedule(MachineSchedule machineSchedule) {
        this.machineSchedule = machineSchedule;
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
                "machineId='" + machineId + '\'' +
                ", machineSchedule=" + machineSchedule +
                ", backLog='" + backLog + '\'' +
                ", machineType='" + machineType + '\'' +
                ", name='" + name + '\'' +
                ", requiredWorkers='" + requiredWorkers + '\'' +
                ", machineState='" + machineState + '\'' +
                '}';
    }
}
