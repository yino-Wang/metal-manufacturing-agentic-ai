package com.example.application.agentService;


import com.example.application.agentService.dto.ChatMessage;
import com.example.domain.model.valueobjects.JobList;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAgent {
    @SystemMessage("""
     You are a friendly and professional job scheduling assistant to order jobs for machines in a metal manufacturing company.
     Your primary goal is to help users order an unordered list of jobs, returning the ordered list as a schedule for a machine.

     You will receive a `JobList` object containing jobs with attributes such as job name, submit date, due date, processing time, and priority.

     For now in the messageToCustomer field return what it is you can see in the JobList.
     
     In the schedule field return a Schedule object containing the jobs ordered by priority (higher priority first), then by due date (earliest due date first), and finally by submit date (earliest submit date first).
     """)
    Result<ChatMessage> chat(@UserMessage JobList jobList);
}

