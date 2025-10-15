package com.example.domain.model.commands;

import java.time.LocalDate;

public class AddJobToMachineCommand {

    private String machineId;
    private int jobTimeNeededDays;
    private int priority;
    private int jobNumber;
    private LocalDate submitDate;
    //private String startDate; //decided by scheduling algorithm
    //private String endDate;   //decided by scheduling algorithm
    private String materialNeeded;
    private int materialAmount;

    public AddJobToMachineCommand(){ }

    public AddJobToMachineCommand(String machineId, int jobTimeNeededDays, int priority, int jobNumber, LocalDate submitDate, String materialNeeded, int materialAmount){
        this.machineId = machineId;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.jobNumber = jobNumber;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
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

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
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


}
