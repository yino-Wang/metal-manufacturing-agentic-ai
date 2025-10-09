package com.example.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class MainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mainScheduleId")
    private Long id;

    @OneToMany(mappedBy = "mainSchedule", cascade = CascadeType.ALL)
    private Set<ScheduledJob> scheduledJobs = new HashSet<>();
}
