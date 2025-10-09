package com.example.domain.model.valueObjects;

import com.example.domain.model.MachineSchedule;
import com.example.domain.model.ScheduledProductionStep;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Machine {

    @OneToOne(mappedBy ="machine", cascade = CascadeType.ALL)
    private MachineSchedule machineSchedule;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    private List<ScheduledProductionStep> scheduledProductionSteps = new ArrayList<>();

    @Column(name="machineType")
    private String machineType;
    @Column(name="name")
    private String name;
    @Column(name="requiredWorkers")
    private String requiredWorkers;
    @Enumerated(EnumType.STRING)
    @Column(name="machineStatus")
    private MachineStatus machineStatus;

    public Machine() {}

    public Machine(String machineType, String name, String requiredWorkers, MachineStatus machineStatus) {
        this.machineType = machineType;
        this.name = name;
        this.requiredWorkers = requiredWorkers;
        this.machineStatus = machineStatus;
    }

    public MachineSchedule getMachineSchedule() {
        return machineSchedule;
    }

    public void setMachineSchedule(MachineSchedule machineSchedule) {
        this.machineSchedule = machineSchedule;
    }

    public List<ScheduledProductionStep> getScheduledProductionSteps() {
        return scheduledProductionSteps;
    }

    public void setScheduledProductionSteps(List<ScheduledProductionStep> scheduledProductionSteps) {
        this.scheduledProductionSteps = scheduledProductionSteps;
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

    public MachineStatus getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(MachineStatus machineStatus) {
        this.machineStatus = machineStatus;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineSchedule=" + machineSchedule +
                ", scheduledProductionSteps=" + scheduledProductionSteps +
                ", machineType='" + machineType + '\'' +
                ", name='" + name + '\'' +
                ", requiredWorkers='" + requiredWorkers + '\'' +
                ", machineStatus=" + machineStatus +
                '}';
    }
}
