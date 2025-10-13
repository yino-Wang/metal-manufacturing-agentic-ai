package com.example.interfaces.rest;

import com.example.application.commandservices.MachineSchedulingCommandService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.SchedulingId;
import com.example.domain.model.valueobjects.Job;
import com.example.interfaces.rest.dto.AddJobToMachineResource;
import com.example.interfaces.rest.transform.AddJobToMachineCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/addJobToMachine")
public class JobAddedToMachineController {

    private MachineSchedulingCommandService machineSchedulingCommandService; // Application Service Dependency
    private MachineSchedulingQueryService machineSchedulingQueryService;

    /**
     * Provide the dependencies
     * @param machineSchedulingCommandService
     */
    public JobAddedToMachineController(MachineSchedulingCommandService machineSchedulingCommandService, MachineSchedulingQueryService machineSchedulingQueryService) {
        this.machineSchedulingCommandService = machineSchedulingCommandService;
        this.machineSchedulingQueryService = machineSchedulingQueryService;
    }

    /**
     * POST method to add a job to a machine
     * @param addJobToMachineResource
     */
    @PostMapping
    @ResponseBody
    public Job addJobToMachine(@RequestBody AddJobToMachineResource addJobToMachineResource) {
        System.out.println("****Job Added to Machine ****" + addJobToMachineResource.getMachineName());
        Job job = machineSchedulingCommandService.addJobToMachine(
                AddJobToMachineCommandDTOAssembler.toCommandFromDTO(addJobToMachineResource));
        return job;
    }

    /**
     * GET method to retrieve all jobs for a scheduling ID
     * @param schedulingId
     * @return Machine
     */
    @GetMapping("/findJobsBySchedulingId")
    @ResponseBody
    public String findAllJobsBySchedulingId(@RequestParam("schedulingId") String schedulingId) {
        System.out.println("****Finding Jobs for Machine SchedulingID ****" + schedulingId);
        Machine machine = machineSchedulingQueryService.findAllJobsBySchedulingId(new SchedulingId(schedulingId));
        return machine.toString();
    }

    /**
     * GET method to retrieve the current job for a scheduling ID
     * @param schedulingId
     * @return Job
     */
    @GetMapping("/findCurrentJobBySchedulingId")
    @ResponseBody
    public Job findCurrentJobBySchedulingId(@RequestParam("schedulingId") String schedulingId) {
        System.out.println("****Finding Current Job for Machine SchedulingID ****" + schedulingId);
        return machineSchedulingQueryService.findCurrentJobBySchedulingId(new SchedulingId(schedulingId));
    }


}
