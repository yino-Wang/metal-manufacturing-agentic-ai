package com.example.interfaces.rest.dto;

public class MaterialAmountByScheduleId {

    private String scheduleId;
    private Long materialAmount;

    public MaterialAmountByScheduleId() {}

    public MaterialAmountByScheduleId(String scheduleId, Long materialAmount) {
        this.scheduleId = scheduleId;
        this.materialAmount = materialAmount;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getMaterialAmount() {
        System.out.println("get material amount ");
        return materialAmount;
    }

    public void setMaterialAmount(Long materialAmount) {
        System.out.println("set material amount ");
        this.materialAmount = materialAmount;
    }
}
