package com.example.domain.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * IndividualSchedule entity class for personal scheduling.
 */
@Entity
@Table(name = "individual_schedule")
public class IndividualSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @Column(name = "next_job")
    private String nextJob;

    @Column(name = "finish_time")
    @Temporal(TemporalType.DATE)
    private Date finishTime;

    @ManyToOne
    @JoinColumn(name = "assigned_employee", referencedColumnName = "employee_id")
    private Employee assignedEmployee;

    // constructor、getter、setter
    public IndividualSchedule() {}

    public IndividualSchedule(Integer scheduleId, String nextJob, Date finishTime, Employee assignedEmployee) {
        this.scheduleId = scheduleId;
        this.nextJob = nextJob;
        this.finishTime = finishTime;
        this.assignedEmployee = assignedEmployee;
    }

    public Integer getScheduleId() {
        return scheduleId; }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId; }

    public String getNextJob() {
        return nextJob; }

    public void setNextJob(String nextJob) {
        this.nextJob = nextJob; }

    public Date getFinishTime() {
        return finishTime; }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime; }

    public Employee getAssignedEmployee() {
        return assignedEmployee; }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee; }

}

