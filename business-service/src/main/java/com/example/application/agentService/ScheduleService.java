package com.example.application.agentService;


import com.example.application.agentService.dto.ChatMessage;
import com.example.domain.model.valueobjects.JobList;
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

    public String generateSchedule(String sessionId, JobList jobList) {
        try {
            log.info("Session [{}]: User message: {}", sessionId, jobList);

            ChatMessage chatMessage = this.chatAgent.chat(sessionId, jobList).content();
            log.info("Session [{}]: Chat agent response: {}", sessionId, chatMessage);

            return chatMessage.messageToCustomer();

        } catch (Exception e) {
            log.error("Session [{}]: Error during scheduling process", sessionId, e);
            return "I'm sorry, but I'm having trouble processing your request right now. Please try again later.";
        }
    }

}
