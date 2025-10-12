package com.example.interfaces.rest.dto;

public class UpdateMachineScheduleResource {

    private String schedulingId;

    public UpdateMachineScheduleResource() {}

    public UpdateMachineScheduleResource(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public String getSchedulingId() {
        return this.schedulingId;
    }

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }
}
