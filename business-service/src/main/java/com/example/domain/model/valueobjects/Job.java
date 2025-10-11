package com.example.domain.model.valueobjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
public class Job {
    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitDate")
    @NotNull
    private Date submitDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startDate")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endDate")
    private Date endDate;
    @Column(name = "materialNeeded")
    private String materialNeeded;
    @Column(name = "materialAmount")
    private Integer materialAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "jobStatus")
    private JobStatus jobStatus;

    public Job() {
    }

    public Job(Date submitDate, Date startDate, Date endDate, String materialNeeded, Integer materialAmount, JobStatus jobStatus) {
        this.submitDate = submitDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobStatus = jobStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
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
