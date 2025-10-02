package com.example;

import java.util.Objects;

public class Employee {
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(name, employee.name) && Objects.equals(jobs, employee.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, jobs);
    }
}
