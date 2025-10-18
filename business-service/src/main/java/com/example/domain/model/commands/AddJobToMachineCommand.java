package com.example.domain.model.commands;

import java.time.LocalDate;

public class AddJobToMachineCommand {

    private String machineId;
    private int jobTimeNeededDays;
    private int priority;
    private int jobNumber;
    private LocalDate dueDate;
    //private String startDate; //decided by scheduling algorithm
    //private String endDate;   //decided by scheduling algorithm
    private String materialNeeded;
    private int materialAmount;
    private String customerName;

    public AddJobToMachineCommand(){ }

    public AddJobToMachineCommand(String machineId, int jobTimeNeededDays, int priority, int jobNumber, LocalDate dueDate, String materialNeeded, int materialAmount, String customerName){
        this.machineId = machineId;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.jobNumber = jobNumber;
        this.dueDate = dueDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.customerName = customerName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
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


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
