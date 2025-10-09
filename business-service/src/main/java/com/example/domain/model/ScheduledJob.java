package com.example.domain.model;

import com.example.domain.model.valueObjects.Status;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ScheduledJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledJobId")
    private Long scheduledJobId;

    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainSchedule_id")
    private MainSchedule mainSchedule;

    @OneToMany(mappedBy = "scheduledJob", cascade = CascadeType.ALL)
    private List<ScheduledProductionStep> scheduledProductionSteps = new ArrayList<>();

    @Column(name = "customer")
    private String customer;

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    // constructor、getter、setter
    public ScheduledJob() {}

    public ScheduledJob(Long scheduledJobId, Employee employee, MainSchedule mainSchedule, List<ScheduledProductionStep> scheduledProductionSteps, String customer, Date dueDate, Integer priority, Status status, Date startDate, Date endDate) {
        this.scheduledJobId = scheduledJobId;
        this.employee = employee;
        this.mainSchedule = mainSchedule;
        this.scheduledProductionSteps = scheduledProductionSteps;
        this.customer = customer;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getScheduledJobId() {
        return scheduledJobId;
    }

    public void setScheduledJobId(Long scheduledJobId) {
        this.scheduledJobId = scheduledJobId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public MainSchedule getMainSchedule() {
        return mainSchedule;
    }

    public void setMainSchedule(MainSchedule mainSchedule) {
        this.mainSchedule = mainSchedule;
    }

    public List<ScheduledProductionStep> getScheduledProductionSteps() {
        return scheduledProductionSteps;
    }

    public void setScheduledProductionSteps(List<ScheduledProductionStep> scheduledProductionSteps) {
        this.scheduledProductionSteps = scheduledProductionSteps;
    }

    public void addScheduledProductionStep(ScheduledProductionStep step) {
        scheduledProductionSteps.add(step);
        step.setScheduledJob(this);
    }

    public void removeScheduledProductionStep(ScheduledProductionStep step) {
        scheduledProductionSteps.remove(step);
        step.setScheduledJob(null);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "scheduledJobId=" + scheduledJobId +
                ", employee=" + employee +
                ", mainSchedule=" + mainSchedule +
                ", scheduledProductionSteps=" + scheduledProductionSteps +
                ", customer='" + customer + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
