package com.example.events.sharedDomain;

public class MachineScheduledEventData {

    private String machineId;
    private String employeeName;

    public MachineScheduledEventData() {}

    public MachineScheduledEventData(String machineId) {
        this.machineId = machineId;
    }

    public MachineScheduledEventData(String machineId, String employeeName) {
        this.machineId = machineId;
        this.employeeName = employeeName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
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
                "schedulingId='" + machineId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
