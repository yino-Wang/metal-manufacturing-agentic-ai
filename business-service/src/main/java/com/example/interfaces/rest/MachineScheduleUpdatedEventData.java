package com.example.interfaces.rest;

public class MachineScheduleUpdatedEventData {

    private String schedulingId;

    public MachineScheduleUpdatedEventData() {
    }

    public MachineScheduleUpdatedEventData(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public String getSchedulingId() {
        return schedulingId;
    }

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    @Override
    public String toString() {
        return "MachineScheduleUpdatedEventData{" +
                "schedulingId='" + schedulingId + '\'' +
                '}';
    }
}
