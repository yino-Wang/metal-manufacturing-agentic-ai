package com.example.domain.model.commands;

// scheduling machine command class
public class ScheduleMachineCommand {

    private String schedulingId;
    private String employeeName;

    public ScheduleMachineCommand() {}

    public ScheduleMachineCommand(String schedulingId, String employeeName) {
        this.schedulingId = schedulingId;
        this.employeeName = employeeName;
    }

    public String getSchedulingId() {
        return schedulingId;
    }
    public void setSchedulingId(String schedulingId) {
        this.schedulingId = schedulingId;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
