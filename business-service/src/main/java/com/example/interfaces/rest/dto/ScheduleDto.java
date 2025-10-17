package com.example.interfaces.rest.dto;

import com.example.domain.model.valueobjects.Job;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDto {
    private List<Job> jobs = new ArrayList<>();

    public ScheduleDto() {}

    public ScheduleDto(List<Job> jobs) {
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
