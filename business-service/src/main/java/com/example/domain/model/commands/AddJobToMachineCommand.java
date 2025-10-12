package com.example.domain.model.commands;

import java.util.Date;

public class AddJobToMachineCommand {

    private String schedulingId;
    private Date submitDate;
    //private String startDate; //decided by scheduling algorithm
    //private String endDate;   //decided by scheduling algorithm
    private String materialNeeded;
    private int materialAmount;

    public AddJobToMachineCommand(){ }

    public AddJobToMachineCommand(String schedulingId, Date submitDate, String materialNeeded, int materialAmount){
        this.schedulingId = schedulingId;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {return schedulingId;}

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
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
