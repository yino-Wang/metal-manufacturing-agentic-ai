package com.example.interfaces.rest.dto;

import java.time.LocalDate;

public class AddJobToMachineResource {
    private int jobNumber;
    private String machineId;
    private int jobTimeNeededDays;
    private int priority;
    private LocalDate dueDate;
    private String materialNeeded;
    private int materialAmount;
    private String customerName;

    public AddJobToMachineResource(){ }

    public AddJobToMachineResource(int jobNumber, int jobTimeNeededDays, int priority, String machineId, String materialName, int materialAmount, String customerName) {
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineId = machineId;
        this.dueDate = LocalDate.now();
        this.materialNeeded = materialName;
        this.materialAmount = materialAmount;
        this.customerName = customerName;
    }

    public AddJobToMachineResource(int jobNumber, int jobTimeNeededDays, int priority, String machineId, LocalDate dueDate, String materialName, int materialAmount, String customerName) {
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineId = machineId;
        this.dueDate = dueDate;
        this.materialNeeded = materialName;
        this.materialAmount = materialAmount;
        this.customerName = customerName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getMaterialNeeded() {
        return materialNeeded;
    }

    public void setMaterialNeeded(String materialNeeded) {
        this.materialNeeded = materialNeeded;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }
    public void setMaterialAmount(int materialAmount) {
        this.materialAmount = materialAmount;
    }

    public int getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public int getJobTimeNeededDays() {
        return jobTimeNeededDays;
    }

    public void setJobTimeNeededDays(int jobTimeNeededDays) {
        this.jobTimeNeededDays = jobTimeNeededDays;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "AddJobToMachineResource{" +
                "jobNumber=" + jobNumber +
                ", machineId='" + machineId + '\'' +
                ", jobTimeNeededDays=" + jobTimeNeededDays +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", materialNeeded='" + materialNeeded + '\'' +
                ", materialAmount=" + materialAmount +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
