package com.example.interfaces.rest.dto;

public class SchedulingIdDto {

    private String schedulingId;

    public SchedulingIdDto() {}

    public SchedulingIdDto(String schedulingId) {
        this.schedulingId = schedulingId;
    }

    public String getSchedulingId() {
        return this.schedulingId;
    }

    @Override
    public String toString() {
        return "SchedulingId{" +
                "schedulingId='" + schedulingId + '\'' +
                '}';
    }
}
