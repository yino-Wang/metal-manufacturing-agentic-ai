package com.example.model.valueObjects;

import com.example.model.MachineSchedule;
import com.example.model.ScheduledProductionStep;
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


}
