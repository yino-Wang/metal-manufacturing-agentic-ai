package com.example.application.agentService;


import com.example.application.agentService.dto.ChatMessage;
import com.example.domain.model.valueobjects.JobList;
import com.example.domain.model.valueobjects.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ChatAgent chatAgent;

    public ScheduleService(ChatAgent chatAgent) {
        this.chatAgent = chatAgent;
    }

    public Schedule generateSchedule(JobList jobList) {
        try {
            log.info("Job List: {}", jobList);

            ChatMessage chatMessage = this.chatAgent.chat(jobList).content();
            log.info("Agent schedule response: {}", chatMessage);

            return chatMessage.schedule();
        } catch (Exception e) {
            log.error("Error during scheduling process");
            return null;
        }
    }

}
