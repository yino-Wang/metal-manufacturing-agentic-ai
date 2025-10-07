package com.example.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * Aggregate Identifier for the com.example.domain.model.aggregates.Job Aggregate
 */
@Embeddable
public class JobScheduleId implements Serializable {

    @Column(name="job_schedule_id")
    private String jobScheduleId;

    public JobScheduleId(){}

    public JobScheduleId(String scheduleId){this.jobScheduleId = scheduleId;}

    public String getJobScheduleId(){return this.jobScheduleId;}
}
