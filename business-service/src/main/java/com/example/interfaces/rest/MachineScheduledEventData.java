package com.example.interfaces.rest;

public class MachineScheduledEventData {

    private String schedulingId;
    private String employeeName;

    public MachineScheduledEventData() {}

    public MachineScheduledEventData(String schedulingId, String employeeName) {
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

    @Override
    public String toString() {
        return "MachineScheduledEventData{" +
                "schedulingId='" + schedulingId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
