package com.example.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class IndividualSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individualScheduleId")
    private Long id;

    @OneToMany(mappedBy = "individualSchedule", cascade = CascadeType.ALL)
    private Set<ScheduledProductionStep> scheduledProductionSteps = new HashSet<>();

    @Column(name = "status")
    private String status;

    @Column(name = "finishTime")
    @Temporal(TemporalType.DATE)
    private Date finishTime;

    @OneToOne(mappedBy = "individualSchedule")
    private Employee employee;
}
