package com.example.domain.model.aggregates;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Job {

    @Column(name = "submitDate")
    private LocalDate dueDate;
    @Column(name = "startDate")
    private LocalDate startDate;
    @Id
    @Column(name = "jobId")
    private Long jobId;
    @Column(name = "endDate")
    private LocalDate endDate;


    @Column(name="jobTimeNeededDays")
    private Integer jobTimeNeededDays;
    @Column(name="priority")
    private Integer priority;
    @Column(name="title")
    private String title;

    public Job() {
    }

public Job(Long jobId, LocalDate dueDate, LocalDate startDate, LocalDate endDate,
           Integer jobTimeNeededDays, Integer priority, String title) {
        this.jobId = jobId;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.title = title;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate submitDate) {
        this.dueDate = submitDate;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
