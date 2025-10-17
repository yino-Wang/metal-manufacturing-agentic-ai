package com.example.domain.model.aggregates;

import com.example.domain.model.valueobjects.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Job {

    @Column(name = "submitDate")
    private LocalDate submitDate;
    @Column(name = "startDate")
    private LocalDate startDate;
    @Id
    @Column(name = "jobId")
    private Long jobId;
    @Column(name = "endDate")
    private LocalDate endDate;
    @Column(name = "materialNeeded")
    private String materialNeeded;
    @Column(name = "materialAmount")
    private Integer materialAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "jobStatus")
    private JobStatus jobStatus;
    @Column(name="jobTimeNeededDays")
    private Integer jobTimeNeededDays;
    @Column(name="priority")
    private Integer priority;
    @Column(name="customerName")
    private String customerName;
    @Column
    private String title;

    public Job() {
    }

    public Job(Long jobId, int jobTimeNeededDays, int priority, LocalDate submitDate, String materialNeeded, Integer materialAmount, JobStatus jobStatus, String customerName) {
        this.jobId = jobId;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobStatus = jobStatus;
        this.customerName = customerName;
    }

    public Job(LocalDate submitDate, LocalDate startDate, LocalDate endDate, String materialNeeded, Integer materialAmount, JobStatus jobStatus, String customerName) {
        this.submitDate = submitDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobStatus = jobStatus;
        this.customerName = customerName;
    }

    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public LocalDate getSubmitDate() {
        return submitDate;
    }
    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
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
    public JobStatus getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    // Add missing methods for jobNumber compatibility
    public Integer getJobNumber() {
        return this.jobId != null ? this.jobId.intValue() : null;
    }

    public void setJobNumber(Integer jobNumber) {
        this.jobId = jobNumber != null ? jobNumber.longValue() : null;
    }

    @Override
    public String toString() {
        return "Job " + jobId + ": STATUS: " + jobStatus + ", PRIORITY: " + priority
                + "\n    submitDate: " + submitDate + ", startDate: " + startDate + ", endDate: " + endDate + ", requiredDuration: " + jobTimeNeededDays
                + "\n    customerName: " + customerName + ", materialNeeded: " + materialNeeded + ", materialAmount: " + materialAmount;
    }


    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
