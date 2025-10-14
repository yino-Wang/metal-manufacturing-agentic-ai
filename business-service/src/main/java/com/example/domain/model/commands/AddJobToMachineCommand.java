package com.example.domain.model.commands;

import java.time.LocalDate;

public class AddJobToMachineCommand {

    private String schedulingId;
    private String machineName;
    private int jobTimeNeededDays;
    private int priority;
    private int jobNumber;
    private LocalDate submitDate;
    //private String startDate; //decided by scheduling algorithm
    //private String endDate;   //decided by scheduling algorithm
    private String materialNeeded;
    private int materialAmount;

    public AddJobToMachineCommand(){ }

    public AddJobToMachineCommand(String machineName, int jobTimeNeededDays, int priority, int jobNumber, LocalDate submitDate, String materialNeeded, int materialAmount){
        //this.schedulingId = schedulingId;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineName = machineName;
        this.jobNumber = jobNumber;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {return schedulingId;}

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
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

    public String getMachineName() {
        return machineName;
    }
    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
