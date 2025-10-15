package com.example.interfaces.rest;

public class MachineScheduleUpdatedEventData {

    private String machineId;

    public MachineScheduleUpdatedEventData() {
    }

    public MachineScheduleUpdatedEventData(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    @Override
    public String toString() {
        return "MachineScheduleUpdatedEventData{" +
                "machineId='" + machineId + '\'' +
                '}';
    }
}
