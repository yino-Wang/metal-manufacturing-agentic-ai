package com.example.model;

import jakarta.persistence.*;

@Entity
public class individualSchedule_job {
    @EmbeddedId
    private individualScheduleJobId id; //made up of both individualSchedule and job ids

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("individualScheduleId")
    @JoinColumn(name = "IndividualScheduleId")
    private IndividualSchedule individualSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobId")
    @JoinColumn(name = "JobId")
    private Job job;

    //can add finish time here?????


    public individualSchedule_job() {
    }

    public individualSchedule_job(individualScheduleJobId id, IndividualSchedule individualSchedule, Job job) {
        this.id = new individualScheduleJobId(individualSchedule.getId(), job.getId());
        this.individualSchedule = individualSchedule;
        this.job = job;
    }

    public individualScheduleJobId getId() {
        return id;
    }

    public void setId(individualScheduleJobId id) {
        this.id = id;
    }

    public IndividualSchedule getIndividualSchedule() {
        return individualSchedule;
    }

    public void setIndividualSchedule(IndividualSchedule individualSchedule) {
        this.individualSchedule = individualSchedule;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
