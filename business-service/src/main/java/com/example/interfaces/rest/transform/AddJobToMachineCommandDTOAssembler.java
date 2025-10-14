package com.example.interfaces.rest.transform;

import com.example.domain.model.commands.AddJobToMachineCommand;
import com.example.interfaces.rest.dto.AddJobToMachineResource;

public class AddJobToMachineCommandDTOAssembler {

    /**
     * Static method within the Assembler class
     * @param addJobToMachineResource
     * @return AddJobToMachineCommand Model
     */
    public static AddJobToMachineCommand toCommandFromDTO(AddJobToMachineResource addJobToMachineResource){
        return new AddJobToMachineCommand(
                //addJobToMachineResource.getSchedulingId(),
                addJobToMachineResource.getMachineName(),
                addJobToMachineResource.getJobTimeNeededDays(),
                addJobToMachineResource.getPriority(),
                addJobToMachineResource.getJobNumber(),
                addJobToMachineResource.getSubmitDate(),
                addJobToMachineResource.getMaterialNeeded(),
                addJobToMachineResource.getMaterialAmount()
        );
    }
}
