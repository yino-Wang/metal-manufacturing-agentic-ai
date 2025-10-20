package com.example.application.agentService;


import com.example.application.agentService.dto.JobListDto;
import com.example.application.agentService.dto.JobMapper;
import com.example.application.agentService.dto.JobSummaryDto;
import com.example.application.agentService.dto.ScheduleMapper;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final MachineRepository machineRepository;
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ChatAgent chatAgent;

    public ScheduleService(MachineRepository machineRepository, ChatAgent chatAgent) {
        this.machineRepository = machineRepository;
        this.chatAgent = chatAgent;
    }

    @Transactional
    public Schedule generateSchedule(String machineId) {
        Machine machine = machineRepository.findByMachineId(new MachineId(machineId));
        JobListDto jobListDto = JobMapper.fromJobList(machine.getJobList());

        log.info("Generating Schedule for {}", machineId);

        try {
            JobListDto agentResponse = this.chatAgent.chat(jobListDto).content();

            ScheduleMapper mapper = new ScheduleMapper(machineRepository);
            Schedule schedule = mapper.fromJobListDto(agentResponse);

            log.info("Agent scheduling response: {}", schedule);

            return schedule;
        } catch (Exception e) {
            log.error("Error during agentic scheduling process", e);

            List<JobSummaryDto> sorted = jobListDto.jobs().stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(
                            JobSummaryDto::dueDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .collect(Collectors.toList());

            JobListDto backupDto = new JobListDto(sorted, LocalDate.now());

            ScheduleMapper mapper = new ScheduleMapper(machineRepository);
            Schedule backupSchedule = mapper.fromJobListDto(backupDto);

            log.info("Backup schedule response: {}", backupSchedule);

            return backupSchedule;
        }
    }

}
