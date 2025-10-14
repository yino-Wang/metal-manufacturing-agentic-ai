package com.example.model;

public class MachineNotFoundException extends RuntimeException {
    public MachineNotFoundException(String machineId) {
        super("Machine " + machineId + " not found");
    }
}
