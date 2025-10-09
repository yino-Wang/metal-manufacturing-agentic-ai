package com.example.domain.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class MainSchedule {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mainScheduleId")
    private Long id;

    @OneToMany(mappedBy = "mainSchedule", cascade = CascadeType.ALL)
    private Set<ScheduledJob> scheduledJobs = new HashSet<>();

    // Constructors, getters, and setters
    public MainSchedule() {}

    public MainSchedule(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ScheduledJob> getScheduledJobs() {
        return scheduledJobs;
    }

    public void setScheduledJobs(Set<ScheduledJob> scheduledJobs) {
        this.scheduledJobs = scheduledJobs;
    }

    public void addScheduledJob(ScheduledJob job) {
        scheduledJobs.add(job);
        job.setMainSchedule(this);
    }

    public void removeScheduledJob(ScheduledJob job) {
        scheduledJobs.remove(job);
        job.setMainSchedule(null);
    }

    @Override
    public String toString() {
        return "MainSchedule{" +
                "id=" + id +
                ", scheduledJobs=" + scheduledJobs +
                '}';
    }
}
