package com.example.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Job entity class representing a scheduled job in the system.
 */
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduledJobId")
    private ScheduledJob scheduledJob;

    @Column(name = "customer")
    private String customer;

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status")
    private String status;

    @Column(name = "submit_date")
    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Column(name = "title")
    private String title;

    // getter, setter
    public Job() {}

    public Integer getJobId() {
        return jobId; }

    public void setJobId(Integer jobId) {
        this.jobId = jobId; }

    public String getCustomer() {
        return customer; }

    public void setCustomer(String customer) {
        this.customer = customer; }

    public Date getDueDate() {
        return dueDate; }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate; }

    public Integer getPriority() {
        return priority; }

    public void setPriority(Integer priority) {
        this.priority = priority; }

    public String getStatus() {
        return status; }

    public void setStatus(String status) {
        this.status = status; }

    public Date getSubmitDate() {
        return submitDate; }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate; }

    public String getTitle() {
        return title; }

    public void setTitle(String title) {
        this.title = title; }

    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", customer='" + customer + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", submitDate=" + submitDate +
                ", title='" + title + '\'' +
                '}';
    }
}

