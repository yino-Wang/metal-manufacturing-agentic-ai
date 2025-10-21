package com.example.controller.dto;

public class AddEmployeeRequest {
    private String name;
    private Float pay; // Hourly rate
    private String skill;
    private String phoneNumber;
    private Float salary; // Monthly/Annual salary
    private String managementArea;
    private String managerName;
    private Boolean manager;
    private String status;

    // Constructors
    public AddEmployeeRequest() {}

    public AddEmployeeRequest(String name, Float pay, String skill, String phoneNumber,
                             Float salary, String managementArea, String managerName, Boolean manager, String status) {
        this.name = name;
        this.pay = pay;
        this.skill = skill;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
        this.managementArea = managementArea;
        this.managerName = managerName;
        this.manager = manager;
        this.status = status;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Float getPay() { return pay; }
    public void setPay(Float pay) { this.pay = pay; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Float getSalary() { return salary; }
    public void setSalary(Float salary) { this.salary = salary; }

    public String getManagementArea() { return managementArea; }
    public void setManagementArea(String managementArea) { this.managementArea = managementArea; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public Boolean getManager() { return manager; }
    public void setManager(Boolean manager) { this.manager = manager; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
