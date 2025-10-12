package com.example.interfaces.rest;

import java.util.Date;

public class JobAddedToMachineEventData {
    private final String schedulingId;
    private final Date submitDate;
    private final String materialNeeded;
    private final int materialAmount;

    public JobAddedToMachineEventData(String schedulingId, Date submitDate, String materialNeeded, int materialAmount) {
        this.schedulingId = schedulingId;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {
        return schedulingId;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public String getMaterialNeeded() {
        return materialNeeded;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }
}


