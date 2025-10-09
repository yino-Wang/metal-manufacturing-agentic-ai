package com.example.domain.model;

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

    public Employee() {}

    public Employee(Long id, String name, IndividualSchedule individualSchedule) {
        this.id = id;
        this.name = name;
        this.individualSchedule = individualSchedule;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IndividualSchedule getIndividualSchedule() {
        return individualSchedule;
    }

    public void setIndividualSchedule(IndividualSchedule individualSchedule) {
        this.individualSchedule = individualSchedule;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", individualSchedule=" + individualSchedule +
                '}';
    }
}
