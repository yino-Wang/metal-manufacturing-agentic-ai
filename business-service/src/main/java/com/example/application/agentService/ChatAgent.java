package com.example.application.agentService;


import com.example.application.agentService.dto.ChatMessage;
import com.example.application.agentService.dto.JobListDto;
import com.example.domain.model.valueobjects.JobList;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAgent {
    @SystemMessage("""
     You are a friendly and professional job scheduling assistant to order jobs for machines in a metal manufacturing company.
     Your primary goal is to help users order an unordered list of jobs, returning the ordered list as a schedule for a machine.

     You will receive a `jobListDto` object containing jobs with attributes such as job name, due date, processing time(jobTimeNeededDays), and priority.

     In the jobListDto field return an array of the jobs ordered by due date (earliest due date first), then by priority (highest priority first).

     Ensure the returned jobListDto is in the same form of the object provided, only containing the reordered list of jobs.
     """)
    Result<ChatMessage> chat(@UserMessage JobListDto jobListDto);
}

