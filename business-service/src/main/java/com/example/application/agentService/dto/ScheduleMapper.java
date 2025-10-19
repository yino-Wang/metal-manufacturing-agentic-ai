package com.example.application.agentService.dto;

import com.example.domain.model.valueobjects.Job;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        Schedule schedule = new Schedule();
        schedule.setJobs(domainJobs);
        return schedule;
    }

    private Job toDomainJob(JobSummaryDto dto) {
        return machineRepository.findJobByJobNumber(dto.jobNumber());
    }
}
