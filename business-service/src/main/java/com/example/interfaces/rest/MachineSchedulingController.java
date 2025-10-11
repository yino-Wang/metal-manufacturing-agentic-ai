package com.example.interfaces.rest;

import com.example.application.commandservices.MachineSchedulingCommandService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.SchedulingId;
import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.interfaces.rest.dto.ScheduleMachineResource;
import com.example.interfaces.rest.transform.ScheduleMachineDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/machinescheduling")
public class MachineSchedulingController {

    private MachineSchedulingCommandService machineSchedulingCommandService; // Application Service Dependency
    private MachineSchedulingQueryService machineSchedulingQueryService;

    /**
     * Provide the dependencies
     * @param machineSchedulingCommandService
     */
    public MachineSchedulingController(MachineSchedulingCommandService machineSchedulingCommandService, MachineSchedulingQueryService machineSchedulingQueryService) {
        this.machineSchedulingCommandService = machineSchedulingCommandService;
        this.machineSchedulingQueryService = machineSchedulingQueryService;
    }

    /**
     * POST method to schedule a machine
     * @param scheduleMachineResource
     */
    @PostMapping
    @ResponseBody
    public SchedulingId scheduleMachine(@RequestBody ScheduleMachineResource scheduleMachineResource) {
        System.out.printf("****Machine Scheduled ****%s%n", scheduleMachineResource.getSchedulingId());
        SchedulingId schedulingId = machineSchedulingCommandService.scheduleMachine(
                ScheduleMachineDTOAssembler.toCommandFromDTO(scheduleMachineResource));
        return schedulingId;
    }

    /**
     * GET method to retrieve a Machine
     * @param schedulingId
     * @return Machine
     */
    @GetMapping("/findMachine")
    @ResponseBody
    public Machine findBySchedulingId(@RequestParam("bookingId") String schedulingId){
        System.out.println("****Machine SchedulingID ****"+schedulingId);
        return machineSchedulingQueryService.find(new SchedulingId(schedulingId));
    }

    /**
     * GET method to retrieve a machine
     * @param
     * @return List<SchedulingId>
     */
    @GetMapping("/findAllSchedulingIds")
    @ResponseBody
    public List<SchedulingId> findAllSchedulingIds(){
        final List<SchedulingId> schedulingIdList = machineSchedulingQueryService.findAllSchedulingIds();
        System.out.println("****Machine SchedulingID ****");
        schedulingIdList.forEach(x->System.out.println(x.getSchedulingId()));
        return schedulingIdList;
    }
}
