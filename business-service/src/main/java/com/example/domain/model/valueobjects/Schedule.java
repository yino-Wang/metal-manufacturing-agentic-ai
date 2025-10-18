package com.example.domain.model.valueobjects;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Schedule {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="schedule_id")
    private List<Job> jobs = new ArrayList<>();

    public Schedule() {}

    public Schedule(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "jobs=" + jobs +
                '}';
    }
}
