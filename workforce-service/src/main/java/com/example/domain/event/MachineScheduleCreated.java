package com.example.domain.event;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Cross-service event message body: Machine schedule created event
 * Received from Business Management service via Kafka
 */
public class MachineScheduleCreated {
    private Long scheduleId;
    private String machineId;
    private String productionLine;
    private Date startTime;
    private Date endTime;
    private String shiftType;
    private int requiredEmployees;
    private String skillRequirements;
    private LocalDateTime eventTimestamp;

    // Constructors
    public MachineScheduleCreated() {
        this.eventTimestamp = LocalDateTime.now();
    }

    public MachineScheduleCreated(Long scheduleId, String machineId, String productionLine,
                                 Date startTime, Date endTime, String shiftType,
                                 int requiredEmployees, String skillRequirements) {
        this();
        this.scheduleId = scheduleId;
        this.machineId = machineId;
        this.productionLine = productionLine;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftType = shiftType;
        this.requiredEmployees = requiredEmployees;
        this.skillRequirements = skillRequirements;
    }

    // Getters and Setters
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(String productionLine) {
        this.productionLine = productionLine;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public int getRequiredEmployees() {
        return requiredEmployees;
    }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees;
    }

    public String getSkillRequirements() {
        return skillRequirements;
    }

    public void setSkillRequirements(String skillRequirements) {
        this.skillRequirements = skillRequirements;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public String toString() {
        return "MachineScheduleCreated{" +
                "scheduleId=" + scheduleId +
                ", machineId='" + machineId + '\'' +
                ", productionLine='" + productionLine + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shiftType='" + shiftType + '\'' +
                ", requiredEmployees=" + requiredEmployees +
                ", skillRequirements='" + skillRequirements + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}
