package com.example.domain.model.valueobjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Job {

    @Column(name = "submitDate")
    @NotNull
    private LocalDate submitDate;
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
    @Column(name = "jobStatus")
    private JobStatus jobStatus;

    public Job() {
    }

    public Job(int jobNumber, LocalDate submitDate, String materialNeeded, Integer materialAmount, JobStatus jobStatus) {
        this.jobNumber = jobNumber;
        this.submitDate = submitDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobStatus = jobStatus;
    }

    public Job(LocalDate submitDate, LocalDate startDate, LocalDate endDate, String materialNeeded, Integer materialAmount, JobStatus jobStatus) {
        this.submitDate = submitDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobStatus = jobStatus;
    }

    public Integer getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(Integer jobNumber) {
        this.jobNumber = jobNumber;
    }
    public LocalDate getSubmitDate() {
        return submitDate;
    }
    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
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
}
