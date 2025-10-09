package com.example.domain.model;

import com.example.domain.model.valueObjects.Status;
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
    private Set<ScheduledProductionStep> scheduledProductionSteps = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @Column(name = "finishTime")
    @Temporal(TemporalType.DATE)
    private Date finishTime;

    @OneToOne(mappedBy = "individualSchedule")
    private Employee employee;

    public IndividualSchedule() {
    }

    public IndividualSchedule(Status status, Date finishTime, Employee employee) {
        this.status = status;
        this.finishTime = finishTime;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ScheduledProductionStep> getScheduledProductionSteps() {
        return scheduledProductionSteps;
    }

    public void setScheduledProductionSteps(Set<ScheduledProductionStep> scheduledProductionSteps) {
        this.scheduledProductionSteps = scheduledProductionSteps;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "IndividualSchedule{" +
                "id=" + id +
                ", scheduledProductionSteps=" + scheduledProductionSteps +
                ", status=" + status +
                ", finishTime=" + finishTime +
                ", employee=" + employee +
                '}';
    }
}
