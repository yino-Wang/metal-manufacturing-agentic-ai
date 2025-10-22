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
        boolean dueDateNull = addJobToMachineResource.getDueDate() == null;
        if (!dueDateNull) {
            currentDate = addJobToMachineResource.getDueDate();
        }
        return new AddJobToMachineCommand(
                addJobToMachineResource.getMachineId(),
                addJobToMachineResource.getJobTimeNeededDays(),
                addJobToMachineResource.getPriority(),
                addJobToMachineResource.getJobNumber(),
                currentDate,
                addJobToMachineResource.getMaterialNeeded(),
                addJobToMachineResource.getMaterialAmount(),
                addJobToMachineResource.getCustomerName()
        );
    }
}
