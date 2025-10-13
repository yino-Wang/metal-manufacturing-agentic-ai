package com.example.interfaces.rest;

public class MachineScheduledEventData {

    private String machineName;
    private String employeeName;

    public MachineScheduledEventData() {}

    public MachineScheduledEventData(String machineName) {
        this.machineName = machineName;
    }

    public MachineScheduledEventData(String machineName, String employeeName) {
        this.machineName = machineName;
        this.employeeName = employeeName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
                "schedulingId='" + machineName + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
