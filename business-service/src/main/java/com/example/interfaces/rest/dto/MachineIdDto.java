package com.example.interfaces.rest.dto;

public class MachineIdDto {

    private String machineId;

    public MachineIdDto() {}

    public MachineIdDto(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineId() {
        return this.machineId;
    }

    @Override
    public String toString() {
        return "MachineId{" +
                "machineId='" + machineId + '\'' +
                '}';
    }
}
