package com.example.application.agentService;


import com.example.application.agentService.dto.JobListDto;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAgent {
    @SystemMessage("""
     You are a friendly and professional job scheduling assistant to order jobs for machines in a metal manufacturing company.
     Your primary goal is to help users order an unordered list of jobs, returning the ordered list as a schedule for a machine.

     You will receive a `jobListDto` object containing jobs with attributes such as job name, due date, processing time(jobTimeNeededDays), and priority.

     In the jobListDto field return an array of the jobs ordered by by priority (lowest number priority first), then due date (earliest due date first).

     Ensure the returned jobListDto is in the same form of the object provided, only containing the reordered list of jobs.
     """)
    Result<JobListDto> chat(@UserMessage JobListDto jobListDto);
}

