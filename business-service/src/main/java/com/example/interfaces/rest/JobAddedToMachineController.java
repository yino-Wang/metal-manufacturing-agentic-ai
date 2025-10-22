package com.example.interfaces.rest;

import com.example.application.agentService.ScheduleService;
import com.example.application.commandservices.MachineSchedulingCommandService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.entities.Job;
import com.example.domain.model.valueobjects.Schedule;
import com.example.interfaces.rest.dto.AddJobToMachineResource;
import com.example.interfaces.rest.transform.AddJobToMachineCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/addJobToMachine")
public class JobAddedToMachineController {

    private MachineSchedulingCommandService machineSchedulingCommandService; // Application Service Dependency
    private MachineSchedulingQueryService machineSchedulingQueryService;
    private ScheduleService scheduleService;

    /**
     * Provide the dependencies
     * @param machineSchedulingCommandService
     */
    public JobAddedToMachineController(MachineSchedulingCommandService machineSchedulingCommandService, MachineSchedulingQueryService machineSchedulingQueryService, ScheduleService scheduleService) {
        this.machineSchedulingCommandService = machineSchedulingCommandService;
        this.machineSchedulingQueryService = machineSchedulingQueryService;
        this.scheduleService = scheduleService;
    }

    /**
     * POST method to add a job to a machine
     * @param addJobToMachineResource
     */
    @PostMapping
    @ResponseBody
    public Job addJobToMachine(@RequestBody AddJobToMachineResource addJobToMachineResource) {
        //System.out.println("****Job Added to Machine ****" + addJobToMachineResource.getMachineId());
        String machineId = addJobToMachineResource.getMachineId();
        Integer jobNumber = addJobToMachineResource.getJobNumber();
        Job job = machineSchedulingCommandService.addJobToMachine(
                AddJobToMachineCommandDTOAssembler.toCommandFromDTO(addJobToMachineResource));
        Schedule updatedSchedule = scheduleService.generateSchedule(machineId);
        List<Job> scheduledJobs = updatedSchedule.getJobs();
        Optional<Job> scheduledJobOpt = scheduledJobs.stream()
                .filter(j -> j.getJobNumber() != null && j.getJobNumber().equals(jobNumber))
                .findFirst();
        return scheduledJobOpt.orElse(job);
    }

    /**
     * GET method to retrieve all jobs for a Machine ID
     * @param machineId
     * @return Machine
     */
    @GetMapping("/findJobsByMachineId")
    @ResponseBody
    public String findAllJobsByMachineId(@RequestParam("machineId") String machineId) {
        //System.out.println("****Finding Jobs for Machine via machineId ****" + machineId);
        Machine machine = machineSchedulingQueryService.findAllJobsByMachineId(new MachineId(machineId));
        return machine.toString();
    }

    /**
     * GET method to retrieve the current job for a Machine ID
     * @param machineId
     * @return Job
     */
    @GetMapping("/findCurrentJobByMachineId")
    @ResponseBody
    public Job findCurrentJobByMachineId(@RequestParam("machineId") String machineId) {
        //System.out.println("****Finding Current Job for Machine Machine id ****" + machineId);
        return machineSchedulingQueryService.findCurrentJobByMachineId(new MachineId(machineId));
    }

    /**
     * GET method to retrieve a job from a job number
     * @param jobNumber
     * @return Job
     */
    @GetMapping("/findJobByJobNumber")
    @ResponseBody
    public Job findJobByJobNumber(@RequestParam("jobNumber") Integer jobNumber) {
        //System.out.println("****Finding Job for given job name ****" + jobNumber);
        return machineSchedulingQueryService.findJobByJobNumber(jobNumber);
    }

    /**
     * GET method to retrieve a job's finish date and status from a job number
     * @param jobNumber
     * @return Job
     */
    @GetMapping("/findJobInfoByJobNumber")
    @ResponseBody
    public String findJobInfoByJobNumber(@RequestParam("jobNumber") Integer jobNumber) {
        //System.out.println("****Finding Job info for given job name ****" + jobNumber);
        Optional<Job> job = machineSchedulingQueryService.findJobInfoByJobNumber(jobNumber);
        if (job.isPresent()) {
            Job foundJob = job.get();
            return "Job Number " + foundJob.getJobNumber()
                    + " info: \n    Projected start date: " + foundJob.getStartDate() + "\n    Projected end date: " + foundJob.getEndDate();
        } else {
            return "Job not in system";
        }
    }

    /**
     * GET method to retrieve a job's finish date and status from a job number
     * @param customerName
     * @return Job
     */
    @GetMapping("/findAllCustomerJobsByCustomerName")
    @ResponseBody
    public String findAllCustomerJobsByCustomerName(@RequestParam("customerName") String customerName) {
        //System.out.println("****Finding all Jobs for given customer name ****" + customerName);
        List<Job> jobs = machineSchedulingQueryService.findAllCustomerJobsByCustomerName(customerName);
        if (jobs.isEmpty()) {
            return "No jobs under customer name " + customerName + " exist in the system";
        } else {
            jobs.sort(Comparator.comparing(Job::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())));
            //jobs.sort(Comparator.nullsLast(Comparator.comparing(Job::getStartDate)));
            StringBuilder outputString = new StringBuilder("All jobs under the customer name " + customerName);
            for (Job j : jobs) {
                outputString.append("\n").append(j);
            }
            return outputString.toString();
        }
    }

    /**
     * GET method to retrieve a job's finish date and status from a job number
     * @param machineId
     * @return Job
     */
    @GetMapping("/findScheduleByMachineId")
    @ResponseBody
    public Machine findScheduleByMachineId(@RequestParam("machineId") String machineId) {
        //System.out.println("****Finding all Jobs for given customer name ****" + customerName);
        Machine machine = machineSchedulingQueryService.findScheduleByMachineId(machineId);
        return machine;
    }



}
