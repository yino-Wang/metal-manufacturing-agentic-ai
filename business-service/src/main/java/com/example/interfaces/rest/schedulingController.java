package com.example.interfaces.rest;

import com.example.application.queryservices.SchedulesQueryService;
import com.example.domain.model.ScheduledJob;
import com.example.domain.model.ScheduledProductionStep;
import com.example.infrastructure.repository.ScheduledJobRepository;
import com.example.infrastructure.repository.ScheduledProductionStepRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller    // This means that this class is a Controller
@RequestMapping("/scheduling")
public class schedulingController {

    private ScheduledProductionStepRepository scheduledProductionStepRepository;
    private ScheduledJobRepository scheduledJobRepository;// Application Service Dependency

    private SchedulesQueryService schedulesQueryService;

    //-----------ScheduledProductionStep -------------------------------------------//
    //get scheduled production step by stepId
    @GetMapping("/scheduledProductionStep/{stepId}")
    @ResponseBody
    public ScheduledProductionStep getScheduledProductionStepById(@PathVariable Long stepId) {
        return scheduledProductionStepRepository.findByStepId(stepId);
    }

    //get all scheduled production steps
    @GetMapping("/scheduledProductionSteps")
    @ResponseBody
    public List<ScheduledProductionStep> getAllScheduledProductionSteps() {
        return scheduledProductionStepRepository.findAll();
    }

    //get all scheduled production step ids
    @GetMapping("/scheduledProductionStepIds")
    @ResponseBody
    public List<Long> getAllScheduledProductionStepIds() {
        return scheduledProductionStepRepository.findAllStepIds();
    }

    //-----------scheduled job -------------------------------------------//
    //get all scheduled job ids
    @GetMapping("/scheduledJobIds")
    @ResponseBody
    public List<Long> getAllScheduledJobIds() {
        return scheduledJobRepository.findAllJobIds();
    }

    //get scheduled job by jobId
    @GetMapping("/scheduledJob/{jobId}")
    @ResponseBody
    public Object getScheduledJobById(@PathVariable Long jobId) {
        return scheduledJobRepository.findByJobId(jobId);
    }

    //get all scheduled jobs
    @GetMapping("/scheduledJobs")
    @ResponseBody
    public List<ScheduledJob> getAllScheduledJobs() {
        return scheduledJobRepository.findAll();
    }


}