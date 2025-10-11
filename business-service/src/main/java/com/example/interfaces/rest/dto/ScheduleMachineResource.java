package com.example.interfaces.rest.dto;


// attributes required to schedule a machine from a user
public class ScheduleMachineResource {
    private String schedulingId;
    private String employeeName;

    public ScheduleMachineResource() {}

    public ScheduleMachineResource(String schedulingId, String employeeName) {
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
