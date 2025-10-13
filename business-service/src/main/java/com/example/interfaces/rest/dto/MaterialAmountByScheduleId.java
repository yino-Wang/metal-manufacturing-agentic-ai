package com.example.interfaces.rest.dto;

public class MaterialAmountByScheduleId {

    private String scheduleId;
    private int materialAmount;

    public MaterialAmountByScheduleId() {}

    public MaterialAmountByScheduleId(String scheduleId, int materialAmount) {
        this.scheduleId = scheduleId;
        this.materialAmount = materialAmount;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(int materialAmount) {
        this.materialAmount = materialAmount;
    }
}
