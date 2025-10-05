package com.example.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Job entity class representing a scheduled job in the system.
 */
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<individualSchedule_job> individualScheduleJobSet = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<ProductionStep> productionSteps = new HashSet<>();

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

    public Job(Long id, Set<individualSchedule_job> individualScheduleJobSet, String customer, Date dueDate, Integer priority, String status, Date submitDate, String title) {
        this.id = id;
        this.individualScheduleJobSet = individualScheduleJobSet;
        this.customer = customer;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.submitDate = submitDate;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<individualSchedule_job> getIndividualScheduleJobSet() {
        return individualScheduleJobSet;
    }

    public void setIndividualScheduleJobSet(Set<individualSchedule_job> individualScheduleJobSet) {
        this.individualScheduleJobSet = individualScheduleJobSet;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + id +
                ", customer='" + customer + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", submitDate=" + submitDate +
                ", title='" + title + '\'' +
                '}';
    }
}

