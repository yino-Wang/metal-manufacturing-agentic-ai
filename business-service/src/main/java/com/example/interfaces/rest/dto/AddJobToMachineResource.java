package com.example.interfaces.rest.dto;

import java.time.LocalDate;

public class AddJobToMachineResource {
    private int jobNumber;
    private String machineName;
    private int jobTimeNeededDays;
    private int priority;
    private LocalDate submitDate;
    private String materialNeeded;
    private int materialAmount;

    public AddJobToMachineResource(){ }

    public AddJobToMachineResource(int jobNumber, int jobTimeNeededDays, int priority, String machineId, String materialName, int materialAmount) {
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineName = machineId;
        this.submitDate = LocalDate.now();
        this.materialNeeded = materialName;
        this.materialAmount = materialAmount;
    }

    public AddJobToMachineResource(int jobNumber, int jobTimeNeededDays, int priority, String machineId, LocalDate submitDate, String materialName, int materialAmount) {
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.machineName = machineId;
        this.submitDate = submitDate;
        this.materialNeeded = materialName;
        this.materialAmount = materialAmount;
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
        return "AddJobToMachineResource{" +
                "jobNumber=" + jobNumber +
                ", machineName='" + machineName + '\'' +
                ", jobTimeNeededDays=" + jobTimeNeededDays +
                ", priority=" + priority +
                ", submitDate=" + submitDate +
                ", materialNeeded='" + materialNeeded + '\'' +
                ", materialAmount=" + materialAmount +
                '}';
    }
}
