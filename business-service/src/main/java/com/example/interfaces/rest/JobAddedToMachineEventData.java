package com.example.interfaces.rest;

import java.time.LocalDate;

public class JobAddedToMachineEventData {
    private String schedulingId;
    private int jobNumber;
    private int jobTimeNeededDays;
    private int priority;
    private String machineName;
    private LocalDate submitDate;
    private String materialNeeded;
    private int materialAmount;

    public JobAddedToMachineEventData(){}

    public JobAddedToMachineEventData(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public JobAddedToMachineEventData(String schedulingId, int jobNumber, int jobTimeNeededDays, int priority, String machineName, LocalDate submitDate, String materialNeeded, int materialAmount) {
        this.schedulingId = schedulingId;
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineName = machineName;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {
        return schedulingId;
    }

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public int getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
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

    @Override
    public String toString() {
        return "JobAddedToMachineEventData{" +
                "schedulingId='" + schedulingId + '\'' +
                ", jobNumber=" + jobNumber +
                ", jobTimeNeededDays=" + jobTimeNeededDays +
                ", priority=" + priority +
                ", machineName='" + machineName + '\'' +
                ", submitDate=" + submitDate +
                ", materialNeeded='" + materialNeeded + '\'' +
                ", materialAmount=" + materialAmount +
                '}';
    }
}


