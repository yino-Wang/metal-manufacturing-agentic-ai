package com.example.domain.model.valueobjects;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Job {

    @Column(name = "dueDate")
    private LocalDate dueDate;
    @Column(name = "startDate")
    private LocalDate startDate;
    @Id
    @Column(name = "jobNumber")
    private Integer jobNumber;
    @Column(name = "endDate")
    private LocalDate endDate;
    @Column(name = "materialNeeded")
    private String materialNeeded;
    @Column(name = "materialAmount")
    private Integer materialAmount;
    @Enumerated(EnumType.STRING)
    @Column(name="jobTimeNeededDays")
    private Integer jobTimeNeededDays;
    @Column(name="priority")
    private Integer priority;
    @Column(name="customerName")
    private String customerName;

    public Job() {
    }

    public Job(int jobNumber, int jobTimeNeededDays, int priority, LocalDate dueDate, String materialNeeded, Integer materialAmount, String customerName) {
        this.jobNumber = jobNumber;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.dueDate = dueDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.customerName = customerName;
    }

    public Job(LocalDate dueDate, LocalDate startDate, LocalDate endDate, String materialNeeded, Integer materialAmount, String customerName) {
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.customerName = customerName;
    }

    public Integer getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(Integer jobNumber) {
        this.jobNumber = jobNumber;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getJobTimeNeededDays() {
        return jobTimeNeededDays;
    }

    public void setJobTimeNeededDays(Integer jobTimeNeededDays) {
        this.jobTimeNeededDays = jobTimeNeededDays;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public String getMaterialNeeded() {
        return materialNeeded;
    }
    public void setMaterialNeeded(String materialNeeded) {
        this.materialNeeded = materialNeeded;
    }
    public Integer getMaterialAmount() {
        return materialAmount;
    }
    public void setMaterialAmount(Integer materialAmount) {
        this.materialAmount = materialAmount;
    }

    @Override
    public String toString() {
        return "Job " + jobNumber + ": PRIORITY: " + priority
                + "\n    dueDate: " + dueDate + ", startDate: " + startDate + ", endDate: " + endDate + ", requiredDuration: " + jobTimeNeededDays
                + "\n    customerName: " + customerName + ", materialNeeded: " + materialNeeded + ", materialAmount: " + materialAmount;
    }
}
