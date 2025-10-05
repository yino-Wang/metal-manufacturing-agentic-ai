package com.example.model;

import com.example.model.valueObjects.Consumable;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class ScheduledProductionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledProductionStepId")
    private Long scheduledProductionStepId;
    @Column(name="productionStepName")
    private String productionStepName;
    //assigned inventory???
    @ElementCollection
    private List<Consumable> reqConsumables;

    @ManyToOne
    @JoinColumn(name="assignedMachine")
    private Machine machine;

    @ManyToOne
    @JoinColumn(name="machineScheduleId")
    private MachineSchedule machineSchedule;

    @ManyToOne
    @JoinColumn(name="scheduledJobId")
    private ScheduledJob scheduledJob;

    @Column(name="startTime")
    private Date startTime;
    @Column(name="endTime")
    private Date endTime;
    @Column(name="status")
    private String status;
    @Column(name="priority")
    private Integer priority;
    @Column(name="queueOrderNumber")
    private Integer queueOrderNumber;

    public ScheduledProductionStep() {
    }

    public ScheduledProductionStep(Long scheduledProductionStepId, String productionStepName,
                                   List<Consumable> reqConsumables, Machine machine, MachineSchedule machineSchedule,
                                   ScheduledJob scheduledJob, Date startTime, Date endTime, String status,
                                   Integer priority, Integer queueOrderNumber) {
        this.scheduledProductionStepId = scheduledProductionStepId;
        this.productionStepName = productionStepName;
        this.reqConsumables = reqConsumables;
        this.machine = machine;
        this.machineSchedule = machineSchedule;
        this.scheduledJob = scheduledJob;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.priority = priority;
        this.queueOrderNumber = queueOrderNumber;
    }

    public Long getScheduledProductionStepId() {
        return scheduledProductionStepId;
    }

    public void setScheduledProductionStepId(Long scheduledProductionStepId) {
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

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
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
                "scheduledProductionStepId=" + scheduledProductionStepId +
                ", productionStepName='" + productionStepName + '\'' +
                ", reqConsumables=" + reqConsumables +
                ", machine=" + machine +
                ", machineSchedule=" + machineSchedule +
                ", scheduledJob=" + scheduledJob +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", queueOrderNumber=" + queueOrderNumber +
                '}';
    }
}
