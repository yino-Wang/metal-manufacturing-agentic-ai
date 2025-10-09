package com.example.domain.model;

import com.example.domain.model.valueObjects.Machine;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MachineSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineScheduleId")
    private Long machineScheduleId;

    @OneToMany(mappedBy = "machineSchedule")
    private List<ScheduledProductionStep> scheduledProductionSteps = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machineId")
    private Machine machine;

    // constructor、getter、setter
    public MachineSchedule() {}

    public MachineSchedule(Long machineScheduleId, List<ScheduledProductionStep> scheduledProductionSteps, Machine machine) {
        this.machineScheduleId = machineScheduleId;
        this.scheduledProductionSteps = scheduledProductionSteps;
        this.machine = machine;
    }

    public Long getMachineScheduleId() {
        return machineScheduleId;
    }

    public void setMachineScheduleId(Long machineScheduleId) {
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
