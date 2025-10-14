package com.example.domain.model.valueobjects;

import com.example.domain.model.entities.Job;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class JobList {

    public static final JobList EMPTY_LIST = new JobList();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="joblist_id")
    @OrderBy("submitDate")
    private List<Job> jobs = new ArrayList<>();

    public JobList() {}

    public JobList(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public String toString() {
        StringBuilder jobToString = new StringBuilder("JobList: \n");
        for (Job j : jobs) {
            jobToString.append(j.toString()).append("\n");
        }
        return jobToString.toString();
    }
}
