package com.example.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class IndividualSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individualScheduleId")
    private Long id;

    @OneToMany(mappedBy = "individualSchedule", cascade = CascadeType.ALL)
    private Set<individualSchedule_job> individualScheduleJobSet = new HashSet<>();

    @Column(name = "status")
    private String status;

    @Column(name = "finishTime")
    @Temporal(TemporalType.DATE)
    private Date finishTime;

    @OneToOne(mappedBy = "individualSchedule")
    private Employee employee;

    public IndividualSchedule() {
    }

    public IndividualSchedule(Long individualScheduleId, String status, Date finishTime, Employee employee) {
        this.id = individualScheduleId;
        this.status = status;
        this.finishTime = finishTime;
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<individualSchedule_job> getIndividualScheduleJobSet() {
        return individualScheduleJobSet;
    }

    public void setIndividualScheduleJobSet(Set<individualSchedule_job> individualScheduleJobSet) {
        this.individualScheduleJobSet = individualScheduleJobSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long individualScheduleId) {
        this.id = individualScheduleId;
    }

    @Override
    public String toString() {
        return "IndividualSchedule{" +
                "individualScheduleId=" + id +
                //", nextJob=" + nextJob +
                ", status='" + status + '\'' +
                ", finishTime=" + finishTime +
                ", employee=" + employee +
                '}';
    }
}
