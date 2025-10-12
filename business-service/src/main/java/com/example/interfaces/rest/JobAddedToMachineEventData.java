package com.example.interfaces.rest;

import java.time.LocalDate;

public class JobAddedToMachineEventData {
    private final String schedulingId;
    private final LocalDate submitDate;
    private final String materialNeeded;
    private final int materialAmount;

    public JobAddedToMachineEventData(String schedulingId, LocalDate submitDate, String materialNeeded, int materialAmount) {
        this.schedulingId = schedulingId;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
    }

    public String getSchedulingId() {
        return schedulingId;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public String getMaterialNeeded() {
        return materialNeeded;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }
}


