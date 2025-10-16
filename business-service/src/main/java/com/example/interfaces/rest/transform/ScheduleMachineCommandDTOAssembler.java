package com.example.interfaces.rest.transform;

import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.interfaces.rest.dto.ScheduleMachineResource;

//bridge between the REST API’s data transfer object (DTO) and the domain command object
public class ScheduleMachineCommandDTOAssembler {

    /**
     * Static method within the Assembler class
     * @param scheduleMachineResource
     * @return ScheduleMachineCommand Model
     */
    public static ScheduleMachineCommand toCommandFromDTO(ScheduleMachineResource scheduleMachineResource){

        return new ScheduleMachineCommand(
                //scheduleMachineResource.getSchedulingId(),
                scheduleMachineResource.getMachineId(),
                scheduleMachineResource.getEmployeeName());
    }
}
