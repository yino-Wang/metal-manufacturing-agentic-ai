package com.example.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue
    @Column(name="employeeId")
    private Integer id;
    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individualScheduleId")
    private IndividualSchedule individualSchedule;

    public Employee() {
    }

    public Employee(Integer id, String name, List<Job> jobs, IndividualSchedule individualSchedule) {
        this.id = id;
        this.name = name;
        this.individualSchedule = individualSchedule;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
