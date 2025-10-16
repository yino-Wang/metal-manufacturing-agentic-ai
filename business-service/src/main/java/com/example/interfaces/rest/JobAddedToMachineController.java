package com.example.interfaces.rest;

import com.example.application.commandservices.MachineSchedulingCommandService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Job;
import com.example.interfaces.rest.dto.AddJobToMachineResource;
import com.example.interfaces.rest.transform.AddJobToMachineCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("****Job Added to Machine ****" + addJobToMachineResource.getMachineId());
        Job job = machineSchedulingCommandService.addJobToMachine(
                AddJobToMachineCommandDTOAssembler.toCommandFromDTO(addJobToMachineResource));
        return job;
    }

    /**
     * GET method to retrieve all jobs for a Machine ID
     * @param machineId
     * @return Machine
     */
    @GetMapping("/findJobsByMachineId")
    @ResponseBody
    public String findAllJobsByMachineId(@RequestParam("machineId") String machineId) {
        System.out.println("****Finding Jobs for Machine via machineId ****" + machineId);
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
        System.out.println("****Finding Current Job for Machine Machine id ****" + machineId);
        return machineSchedulingQueryService.findCurrentJobByMachineId(new MachineId(machineId));
    }


}
