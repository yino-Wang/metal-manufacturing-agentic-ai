package com.example.interfaces.rest;

import com.example.application.commandservices.MachineSchedulingCommandService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.interfaces.rest.dto.ScheduleMachineResource;
import com.example.interfaces.rest.transform.ScheduleMachineCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/machinescheduling")
public class MachineScheduledController {

    private MachineSchedulingCommandService machineSchedulingCommandService; // Application Service Dependency
    private MachineSchedulingQueryService machineSchedulingQueryService;

    /**
     * Provide the dependencies
     * @param machineSchedulingCommandService
     */
    public MachineScheduledController(MachineSchedulingCommandService machineSchedulingCommandService, MachineSchedulingQueryService machineSchedulingQueryService) {
        this.machineSchedulingCommandService = machineSchedulingCommandService;
        this.machineSchedulingQueryService = machineSchedulingQueryService;
    }

    /**
     * POST method to schedule a machine
     * @param scheduleMachineResource
     */
    @PostMapping
    @ResponseBody
    public MachineId scheduleMachine(@RequestBody ScheduleMachineResource scheduleMachineResource) {
        System.out.printf("****Machine MachineId ****%s%n", scheduleMachineResource.getMachineId());
        MachineId machineId = machineSchedulingCommandService.scheduleMachine(
                ScheduleMachineCommandDTOAssembler.toCommandFromDTO(scheduleMachineResource));
        return machineId;
    }

    /**
     * GET method to retrieve a Machine
     * @param machineId
     * @return Machine
     */
    @GetMapping("/findMachine")
    @ResponseBody
    public Machine findByMachineId(@RequestParam("machineId") String machineId){
        System.out.println("****Machine SchedulingID ****"+machineId);
        return machineSchedulingQueryService.find(new MachineId(machineId));
    }

    /**
     * GET method to retrieve a list of SchedulingIds
     * @param
     * @return List<MachineId>
     */
    @GetMapping("/findAllMachineIds")
    @ResponseBody
    public List<MachineId> findAllMachineIds(){
        final List<MachineId> machineIdList = machineSchedulingQueryService.findAllMachineId();
        System.out.println("****Machine SchedulingID ****");
        machineIdList.forEach(x->System.out.println(x.getMachineId()));
        return machineIdList;
    }
}
