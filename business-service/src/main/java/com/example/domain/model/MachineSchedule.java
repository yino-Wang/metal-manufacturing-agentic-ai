package com.example.domain.model;

import com.example.domain.model.valueObjects.Machine;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MachineSchedule {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineScheduleId")
    private String machineScheduleId;

    @OneToMany(mappedBy = "machineSchedule")
    private List<ScheduledProductionStep> scheduledProductionSteps = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machineId")
    private Machine machine;

    // constructor、getter、setter
    public MachineSchedule() {}

    public MachineSchedule(String machineScheduleId, List<ScheduledProductionStep> scheduledProductionSteps, Machine machine) {
        this.machineScheduleId = machineScheduleId;
        this.scheduledProductionSteps = scheduledProductionSteps;
        this.machine = machine;
    }

    public String getMachineScheduleId() {
        return machineScheduleId;
    }

    public void setMachineScheduleId(String machineScheduleId) {
        this.machineScheduleId = machineScheduleId;
    }

    public List<ScheduledProductionStep> getScheduledProductionSteps() {
        return scheduledProductionSteps;
    }

    public void setScheduledProductionSteps(List<ScheduledProductionStep> scheduledProductionSteps) {
        this.scheduledProductionSteps = scheduledProductionSteps;
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
                ", scheduledProductionSteps=" + scheduledProductionSteps +
                ", machine=" + machine +
                '}';
    }
}
