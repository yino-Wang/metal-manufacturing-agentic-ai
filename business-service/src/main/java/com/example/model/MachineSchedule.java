package com.example.model;

import com.example.model.valueObjects.Machine;
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


}
