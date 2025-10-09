package com.example.model;

import jakarta.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeId")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToOne
    @JoinColumn(name = "individualScheduleId")
    private IndividualSchedule individualSchedule;



}
