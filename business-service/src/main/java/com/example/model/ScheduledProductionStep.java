package com.example.model;

import com.example.model.valueObjects.Consumable;
import com.example.model.valueObjects.Machine;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class ScheduledProductionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledProductionStepId")
    private Long id;

    @Column(name = "stepName")
    private String stepName;

    @Column(name = "machineHours")
    private Integer machineHours;
    @Column(name = "manHours")
    private Integer manHours;
    @ElementCollection
    private List<Consumable> reqConsumables;
    @ManyToOne
    @JoinColumn(name = "requiredMachine")
    private Machine machine;
    @Embedded
    private Employee employee;
    @Column(name = "reqMachineType")
    private String reqMachineType;
    //DO NOT NEED?????? MAKES IT EMBEDDABLE IF NOT HAVE
//    @Column(name="positionInMachineSchedule")
//    private Integer positionInMachineSchedule;
    @Column(name = "dependentOn")
    private String dependentOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId")
    private ScheduledJob scheduledJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individualScheduleId")
    private IndividualSchedule individualSchedule;

    @ManyToOne
    @JoinColumn(name = "machineScheduleId")
    private MachineSchedule machineSchedule;

    @Column(name = "startTime")
    private Date startTime;
    @Column(name = "endTime")
    private Date endTime;
    @Column(name = "status")
    private String status;
    @Column(name = "priority")
    private Integer priority;
    @Column(name = "queueOrderNumber")
    private Integer queueOrderNumber;



}


