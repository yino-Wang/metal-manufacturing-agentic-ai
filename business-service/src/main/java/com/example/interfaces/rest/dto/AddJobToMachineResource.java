package com.example.interfaces.rest.dto;

import java.time.LocalDate;

public class AddJobToMachineResource {
    private String schedulingId;
    private LocalDate submitDate;
    private String materialNeeded;
    private int materialAmount;

    public AddJobToMachineResource(){ }

    public AddJobToMachineResource(String schedulingId, LocalDate submitDate, String materialNeeded, int materialAmount){
        this.schedulingId = schedulingId;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {return schedulingId;}

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
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
}
