package com.example.application.agentService.dto;

import com.example.domain.model.valueobjects.Job;
import com.example.domain.model.valueobjects.JobList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class JobMapper {

    private JobMapper() {}

    public static JobListDto fromJobList(JobList jobList) {
        if (jobList == null) {
            return new JobListDto(List.of());
        }

        // adapt this access if your JobList exposes a different method name (e.g. jobs() vs getJobs())
        List<Job> domainJobs = jobList.getJobs();

        List<JobSummaryDto> summaries = (domainJobs == null) ? List.of() :
                domainJobs.stream()
                        .filter(Objects::nonNull)
                        .map(j -> new JobSummaryDto(
                                j.getDueDate(),
                                j.getJobNumber(),
                                j.getJobTimeNeededDays(),
                                j.getPriority()
                        ))
                        .collect(Collectors.toList());

        return new JobListDto(List.copyOf(summaries));
    }
}