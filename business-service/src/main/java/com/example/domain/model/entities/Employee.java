package com.example.domain.model.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class Employee {

    private String employeeName;

    public Employee() {}
    public Employee(String employeeName) {
        this.employeeName = employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getEmployeeName() {
        return this.employeeName;
    }
}
