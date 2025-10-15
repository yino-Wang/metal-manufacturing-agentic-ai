package com.example.interfaces.rest.transform;

import com.example.domain.model.commands.AddJobToMachineCommand;
import com.example.interfaces.rest.dto.AddJobToMachineResource;
import java.time.LocalDate;

public class AddJobToMachineCommandDTOAssembler {

    /**
     * Static method within the Assembler class
     * @param addJobToMachineResource
     * @return AddJobToMachineCommand Model
     */
    public static AddJobToMachineCommand toCommandFromDTO(AddJobToMachineResource addJobToMachineResource){
        LocalDate currentDate = LocalDate.now();
        boolean submitDateNull = addJobToMachineResource.getSubmitDate() == null;
        if (!submitDateNull) {
            currentDate = addJobToMachineResource.getSubmitDate();
        }
        return new AddJobToMachineCommand(
                //addJobToMachineResource.getSchedulingId(),
                addJobToMachineResource.getMachineId(),
                addJobToMachineResource.getJobTimeNeededDays(),
                addJobToMachineResource.getPriority(),
                addJobToMachineResource.getJobNumber(),
                currentDate,
                addJobToMachineResource.getMaterialNeeded(),
                addJobToMachineResource.getMaterialAmount()
        );
    }
}
