package com.example.domain.model;

import com.example.domain.model.valueObjects.Consumable;
import com.example.domain.model.valueObjects.Machine;
import com.example.domain.model.valueObjects.Status;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class ScheduledProductionStep {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledProductionStepId")
    private Long id;

    @Column(name = "stepName")
    private String stepName;

    @Column(name = "machineHours")
    private Integer machineHours;
    @Column(name = "manHours")
    private Integer manHours;
    @ElementCollection
    private List<Consumable> reqConsumables;
    @ManyToOne
    @JoinColumn(name = "requiredMachine")
    private Machine machine;
    @Embedded
    private Employee employee;
    @Column(name = "reqMachineType")
    private String reqMachineType;
    //DO NOT NEED?????? MAKES IT EMBEDDABLE IF NOT HAVE
//    @Column(name="positionInMachineSchedule")
//    private Integer positionInMachineSchedule;
    @Column(name = "dependentOn")
    private ScheduledProductionStep dependentOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId")
    private ScheduledJob scheduledJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individualScheduleId")
    private IndividualSchedule individualSchedule;

    @ManyToOne
    @JoinColumn(name = "machineScheduleId")
    private MachineSchedule machineSchedule;

    @Column(name = "startTime")
    private Date startTime;
    @Column(name = "endTime")
    private Date endTime;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;
    @Column(name = "priority")
    private Integer priority;
    @Column(name = "queueOrderNumber")
    private Integer queueOrderNumber;

    public ScheduledProductionStep() {}

    public ScheduledProductionStep(Long id, String stepName, Integer machineHours, Integer manHours, List<Consumable> reqConsumables, Machine machine, Employee employee, String reqMachineType, ScheduledProductionStep dependentOn, ScheduledJob scheduledJob, IndividualSchedule individualSchedule, MachineSchedule machineSchedule, Date startTime, Date endTime, Status status, Integer priority, Integer queueOrderNumber) {
        this.id = id;
        this.stepName = stepName;
        this.machineHours = machineHours;
        this.manHours = manHours;
        this.reqConsumables = reqConsumables;
        this.machine = machine;
        this.employee = employee;
        this.reqMachineType = reqMachineType;
        this.dependentOn = dependentOn;
        this.scheduledJob = scheduledJob;
        this.individualSchedule = individualSchedule;
        this.machineSchedule = machineSchedule;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.priority = priority;
        this.queueOrderNumber = queueOrderNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getMachineHours() {
        return machineHours;
    }

    public void setMachineHours(Integer machineHours) {
        this.machineHours = machineHours;
    }

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public List<Consumable> getReqConsumables() {
        return reqConsumables;
    }

    public void setReqConsumables(List<Consumable> reqConsumables) {
        this.reqConsumables = reqConsumables;
    }

    public void addConsumable(Consumable consumable) {
        this.reqConsumables.add(consumable);
    }

    public void removeConsumable(Consumable consumable) {
        this.reqConsumables.remove(consumable);
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getReqMachineType() {
        return reqMachineType;
    }

    public void setReqMachineType(String reqMachineType) {
        this.reqMachineType = reqMachineType;
    }

    public ScheduledProductionStep getDependentOn() {
        return dependentOn;
    }

    public void setDependentOn(ScheduledProductionStep dependentOn) {
        this.dependentOn = dependentOn;
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    public IndividualSchedule getIndividualSchedule() {
        return individualSchedule;
    }

    public void setIndividualSchedule(IndividualSchedule individualSchedule) {
        this.individualSchedule = individualSchedule;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
        return "ScheduledProductionStep{" +
                "id=" + id +
                ", stepName='" + stepName + '\'' +
                ", machineHours=" + machineHours +
                ", manHours=" + manHours +
                ", reqConsumables=" + reqConsumables +
                ", machine=" + machine +
                ", employee=" + employee +
                ", reqMachineType='" + reqMachineType + '\'' +
                ", dependentOn='" + dependentOn + '\'' +
                ", scheduledJob=" + scheduledJob +
                ", individualSchedule=" + individualSchedule +
                ", machineSchedule=" + machineSchedule +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", priority=" + priority +
                ", queueOrderNumber=" + queueOrderNumber +
                '}';
    }
}


