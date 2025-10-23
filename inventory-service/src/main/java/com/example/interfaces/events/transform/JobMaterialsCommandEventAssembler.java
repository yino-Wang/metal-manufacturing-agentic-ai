package com.example.interfaces.events.transform;

import com.example.domain.commands.AddJobMaterialsCommand;
import com.example.events.sharedDomain.JobAddedToMachineEvent;
import com.example.events.sharedDomain.JobAddedToMachineEventData;


public class JobMaterialsCommandEventAssembler {
    /**
     * Static method within the Assembler class
     * @param jobAddedToMachineEvent
     * @return AssignTrackingNumberCommand Model
     */
    public static AddJobMaterialsCommand toCommandFromEvent(JobAddedToMachineEvent jobAddedToMachineEvent){
        JobAddedToMachineEventData eventData = jobAddedToMachineEvent.getJobAddedToMachineEventData();
        return new AddJobMaterialsCommand(
                eventData.getJobNumber(),
                eventData.getMaterialNeeded(),
                eventData.getMaterialAmount());
    }
}
