package com.example.interfaces.rest;

import java.time.LocalDate;

public class JobAddedToMachineEventData {
    private String machineId;
    private int jobNumber;
    private int jobTimeNeededDays;
    private int priority;
    private LocalDate submitDate;
    private String materialNeeded;
    private int materialAmount;

    public JobAddedToMachineEventData(){}

    public JobAddedToMachineEventData(String machineId) {
        this.machineId = machineId;
    }

    public JobAddedToMachineEventData(String machineId, int jobNumber, int jobTimeNeededDays, int priority, LocalDate submitDate, String materialNeeded, int materialAmount) {
        this.machineId = machineId;
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
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

    public int getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
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
                "schedulingId='" + machineId + '\'' +
                ", jobNumber=" + jobNumber +
                ", jobTimeNeededDays=" + jobTimeNeededDays +
                ", priority=" + priority +
                ", submitDate=" + submitDate +
                ", materialNeeded='" + materialNeeded + '\'' +
                ", materialAmount=" + materialAmount +
                '}';
    }
}


