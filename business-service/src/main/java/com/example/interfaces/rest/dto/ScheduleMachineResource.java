package com.example.interfaces.rest.dto;


// attributes required to schedule a machine from a user
public class ScheduleMachineResource {
    private String machineId;

    public ScheduleMachineResource() {}

    public ScheduleMachineResource(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
