package com.example.application.agentService;


import com.example.application.agentService.dto.JobListDto;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAgent {
    @SystemMessage("""
     Your primary goal is to help users order an unordered list of jobs, returning the ordered list as a schedule for a machine.

     You will receive a `jobListDto` object containing jobs with attributes such as job name, due date, processing time(jobTimeNeededDays), and priority, as well as the current date.
     
     Your task is to order these jobs into a schedule that minimizes late completions while respecting job priorities and due dates.
     
     These jobs will be completed one job at a time, in the order you provide. A job can only start once the previous job is completed.
     For example if the first job takes 3 days to complete, the second job can only start on day 4 (current date plus 4). And the third job can only start once the second job is completed, and so on.
     
     The priority is represented as a number, with lower numbers indicating higher priority (e.g., priority 1 is higher than priority 2).
     The due date indicates the date by which the job should ideally be completed.
     The processing time (jobTimeNeededDays) indicates how many days it takes to complete the job.
     
     We desire to schedule the jobs in a way that respects their priority and due dates as much as possible.
     Remaining within due date is most important, followed by priority. If exceeding a job's due date can be avoided, do so even if it means scheduling a lower priority job first.
     However if all jobs due dates can be met, order higher priority jobs first.
     
     If it is unavoidable to exceed a due date, order jobs to minimise total time exceeding due date, regardless of priority.
     
     In the jobListDto field return the array of the jobs.
     Ensure the returned jobListDto is in the same form of the object provided, only changing the order of the list of jobs.
     """)
    Result<JobListDto> chat(@UserMessage JobListDto jobListDto);
}

