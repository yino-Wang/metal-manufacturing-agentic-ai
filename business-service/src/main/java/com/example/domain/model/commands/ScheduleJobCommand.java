package com.example.domain.model.commands;

import java.util.Date;

/**
 * Book Cargo Command class
 */
public class ScheduleJobCommand {

    private String jobScheduleId;
    private int queuePosition;
    private String scheduleOrder;
    private Long firstProductionStepId;
    private String firstProductionStep;
    private Long nextProductionStepId;
    private String nextProductionStep;
    private Date submitDate;
    private Date startDate;
    private Date projectedEndDate;

    public ScheduleJobCommand(){}

    public ScheduleJobCommand(String jobScheduleId, int queuePosition, String scheduleOrder, Long firstProductionStepId,
                              String firstProductionStep, Long nextProductionStepId, String nextProductionStep,
                              Date submitDate, Date startDate, Date projectedEndDate) {
        this.jobScheduleId = jobScheduleId;
        this.queuePosition = queuePosition;
        this.scheduleOrder = scheduleOrder;
        this.firstProductionStepId = firstProductionStepId;
        this.firstProductionStep = firstProductionStep;
        this.nextProductionStepId = nextProductionStepId;
        this.nextProductionStep = nextProductionStep;
        this.submitDate = submitDate;
        this.startDate = startDate;
        this.projectedEndDate = projectedEndDate;
    }

    public String getJobScheduleId() { return jobScheduleId; }

    public void setJobScheduleId(String jobScheduleId) {
        this.jobScheduleId = jobScheduleId;
    }

    public Long getFirstProductionStepId() {
        return firstProductionStepId;
    }

    public void setFirstProductionStepId(Long firstProductionStepId) {
        this.firstProductionStepId = firstProductionStepId;
    }

    public Long getNextProductionStepId() {
        return nextProductionStepId;
    }

    public void setNextProductionStepId(Long nextProductionStepId) {
        this.nextProductionStepId = nextProductionStepId;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getScheduleOrder() {
        return scheduleOrder;
    }

    public void setScheduleOrder(String scheduleOrder) {
        this.scheduleOrder = scheduleOrder;
    }

    public String getFirstProductionStep() {
        return firstProductionStep;
    }

    public void setFirstProductionStep(String firstProductionStep) {
        this.firstProductionStep = firstProductionStep;
    }

    public String getNextProductionStep() {
        return nextProductionStep;
    }

    public void setNextProductionStep(String nextProductionStep) {
        this.nextProductionStep = nextProductionStep;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getProjectedEndDate() {
        return projectedEndDate;
    }

    public void setProjectedEndDate(Date projectedEndDate) {
        this.projectedEndDate = projectedEndDate;
    }
}
