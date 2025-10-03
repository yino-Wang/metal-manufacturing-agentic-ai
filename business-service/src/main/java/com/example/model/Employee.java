package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Employee {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String jobs;

    public Employee() {
    }

    public Employee(Integer id, String name, String jobs) {
        this.id = id;
        this.name = name;
        this.jobs = jobs;
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

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
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
