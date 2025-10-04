package com.example.model;

import jakarta.persistence.*;

import java.util.Date;

public class ScheduledJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledJobId")
    private Integer scheduledJobId;

    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @OneToOne(mappedBy = "scheduledJobId")
    private Job job;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status")
    private String status;

    @Column(name = "material_requirement")
    private Integer materialRequirement;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "queue_order_number")
    private Integer queueOrderNumber;

    public ScheduledJob() {}

    public ScheduledJob(Integer jobId, Employee employee, String customer, Date dueDate, Integer priority,
                        String status, Date submitDate, String title, Integer materialRequirement, Date startDate,
                        Date endDate, Integer queueOrderNumber) {
        this.scheduledJobId = jobId;
        this.employee = employee;
        this.priority = priority;
        this.status = status;
        this.materialRequirement = materialRequirement;
        this.startDate = startDate;
        this.endDate = endDate;
        this.queueOrderNumber = queueOrderNumber;
    }

    public Integer getScheduledJobId() {
        return scheduledJobId;
    }

    public void setScheduledJobId(Integer scheduledJobId) {
        this.scheduledJobId = scheduledJobId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMaterialRequirement() {
        return materialRequirement;
    }

    public void setMaterialRequirement(Integer materialRequirement) {
        this.materialRequirement = materialRequirement;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getQueueOrderNumber() {
        return queueOrderNumber;
    }

    public void setQueueOrderNumber(Integer queueOrderNumber) {
        this.queueOrderNumber = queueOrderNumber;
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "scheduledJobId=" + scheduledJobId +
                ", employee=" + employee +
                ", job=" + job +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", materialRequirement=" + materialRequirement +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", queueOrderNumber=" + queueOrderNumber +
                '}';
    }
}
