package com.example.domain.model.valueobjects;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
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

}
