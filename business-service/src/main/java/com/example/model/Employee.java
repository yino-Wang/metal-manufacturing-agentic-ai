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
    @OneToMany(mappedBy = "employeeId")
    private List<Job> jobs;

    public Employee() {
    }

    public Employee(Integer id, String name, List<Job> jobs) {
        this.id = id;
        this.name = name;
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

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + "\'" +
                ", jobs='" + jobs + "\'" +
                '}';

    }
}
