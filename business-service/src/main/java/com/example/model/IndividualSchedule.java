package com.example.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class IndividualSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individualScheduleId")
    private Integer individualScheduleId;

    @Column(name = "nextJob")
    private Job nextJob;

    @Column(name = "status")
    private String status;

    @Column(name = "finishTime")
    @Temporal(TemporalType.DATE)
    private Date finishTime;

    @OneToOne(mappedBy = "individualSchedule")
    private Employee employee;

    public IndividualSchedule() {
    }

    public IndividualSchedule(Integer individualScheduleId, Job nextJob, String status, Date finishTime, Employee employee) {
        this.individualScheduleId = individualScheduleId;
        this.nextJob = nextJob;
        this.status = status;
        this.finishTime = finishTime;
        this.employee = employee;
    }

    public Integer getIndividualScheduleId() {
        return individualScheduleId;
    }

    public void setIndividualScheduleId(Integer individualScheduleId) {
        this.individualScheduleId = individualScheduleId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Job getNextJob() {
        return nextJob;
    }

    public void setNextJob(Job nextJob) {
        this.nextJob = nextJob;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "IndividualSchedule{" +
                "individualScheduleId=" + individualScheduleId +
                ", employee=" + employee +
                ", nextJob=" + nextJob +
                ", status='" + status + '\'' +
                ", finishTime=" + finishTime +
                '}';
    }
}
