package com.example.application.agentService;


import com.example.application.agentService.dto.ChatMessage;
import com.example.application.agentService.dto.JobListDto;
import com.example.application.agentService.dto.JobMapper;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.JobList;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        try {
            log.info("Generating Schedule for {}, Job List: {}", machineId, jobListDto);

            ChatMessage chatMessage = this.chatAgent.chat(jobListDto).content();
            log.info("Agent schedule response: {}", chatMessage);

            return new Schedule();
        } catch (Exception e) {
            log.error("Error during scheduling process");
            return null;
        }
    }

}
