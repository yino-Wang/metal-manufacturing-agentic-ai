package com.example.application.agentService.dto;

import com.example.domain.model.entities.Job;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ScheduleMapper {

    public ScheduleMapper(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    private final MachineRepository machineRepository;

    public Schedule fromJobListDto(JobListDto jobListDto) {
        if (jobListDto == null || jobListDto.jobs() == null) {
            return new Schedule();
        }

        List<Job> domainJobs = jobListDto.jobs().stream()
                .filter(Objects::nonNull)
                .map(this::toDomainJob)
                .collect(Collectors.toList());

        LocalDate currentStart = LocalDate.now();
        for (Job job : domainJobs) {
            int days = job.getJobTimeNeededDays() != null ? job.getJobTimeNeededDays() : 0;
            job.setStartDate(currentStart);
            job.setEndDate(currentStart.plusDays(days));
            currentStart = job.getEndDate();
        }

        Schedule schedule = new Schedule();
        schedule.setJobs(domainJobs);



        return schedule;
    }

    private Job toDomainJob(JobSummaryDto dto) {
        return machineRepository.findJobByJobNumber(dto.jobNumber());
    }
}
