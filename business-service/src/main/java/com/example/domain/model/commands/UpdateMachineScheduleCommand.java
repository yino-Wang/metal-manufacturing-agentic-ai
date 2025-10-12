package com.example.domain.model.commands;

public class UpdateMachineScheduleCommand {

    private String schedulingId;

    public UpdateMachineScheduleCommand() {}

    public UpdateMachineScheduleCommand(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public String getSchedulingId() {
        return this.schedulingId;
    }

    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }


}
