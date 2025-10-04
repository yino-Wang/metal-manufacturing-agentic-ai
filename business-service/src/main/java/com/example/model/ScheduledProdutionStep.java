package com.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Embeddable
public class ScheduledProdutionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledProductionStepId")
    private String scheduledProductionStepId;
    @Column(name="productionStepName")
    private String productionStepName;

    //assigned inventory???
    @OneToMany(mappedBy = "scheduledProductionStep")
    private List<Consumable> reqConsumables = new ArrayList<>();
    @Column(name="assignedMachine")
    private Machine machine;
    @Column(name="machineScheduleId")
    private MachineSchedule machineSchedule;
    @Column(name="startTime")
    private Date startTime;
    @Column(name="endTime")
    private Date endTime;
    @Column(name="scheduledJobId")
    private ScheduledJob scheduledJob;
    @Column(name="status")
    private String status;
    @Column(name="priority")
    private Integer priority;
    @Column(name="queueOrderNumber")
    private Integer queueOrderNumber;

    public ScheduledProdutionStep() {
    }

    public ScheduledProdutionStep(String scheduledProductionStepId, String productionStepName, List<Consumable> reqConsumables,
                                  Machine machine, MachineSchedule machineSchedule, Date startTime, Date endTime,
                                  ScheduledJob scheduledJob, String status, Integer priority, Integer queueOrderNumber) {
        this.scheduledProductionStepId = scheduledProductionStepId;
        this.productionStepName = productionStepName;
        this.reqConsumables = reqConsumables;
        this.machine = machine;
        this.machineSchedule = machineSchedule;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduledJob = scheduledJob;
        this.status = status;
        this.priority = priority;
        this.queueOrderNumber = queueOrderNumber;
    }

    public String getScheduledProductionStepId() {
        return scheduledProductionStepId;
    }

    public void setScheduledProductionStepId(String scheduledProductionStepId) {
        this.scheduledProductionStepId = scheduledProductionStepId;
    }

    public String getProductionStepName() {
        return productionStepName;
    }

    public void setProductionStepName(String productionStepName) {
        this.productionStepName = productionStepName;
    }

    public List<Consumable> getReqConsumables() {
        return reqConsumables;
    }

    public void setReqConsumables(List<Consumable> reqConsumables) {
        this.reqConsumables = reqConsumables;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public MachineSchedule getMachineSchedule() {
        return machineSchedule;
    }

    public void setMachineSchedule(MachineSchedule machineSchedule) {
        this.machineSchedule = machineSchedule;
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

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getQueueOrderNumber() {
        return queueOrderNumber;
    }

    public void setQueueOrderNumber(Integer queueOrderNumber) {
        this.queueOrderNumber = queueOrderNumber;
    }

    @Override
    public String toString() {
        return "ScheduledProdutionStep{" +
                "scheduledProductionStepId='" + scheduledProductionStepId + '\'' +
                ", productionStepName='" + productionStepName + '\'' +
                ", reqConsumables=" + reqConsumables +
                ", machine=" + machine +
                ", machineSchedule=" + machineSchedule +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", scheduledJob=" + scheduledJob +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", queueOrderNumber=" + queueOrderNumber +
                '}';
    }
}
