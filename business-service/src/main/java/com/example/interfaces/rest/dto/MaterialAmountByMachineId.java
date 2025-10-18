package com.example.interfaces.rest.dto;

public class MaterialAmountByMachineId {

    private String machineId;
    private Long materialAmount;

    public MaterialAmountByMachineId() {}

    public MaterialAmountByMachineId(String machineId, Long materialAmount) {
        this.machineId = machineId;
        this.materialAmount = materialAmount;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Long getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(Long materialAmount) {
        this.materialAmount = materialAmount;
    }
}
