package com.example.interfaces.events;

import com.example.application.AllocateMaterialsCommandService;
import com.example.events.sharedDomain.JobAddedToMachineEvent;
import com.example.interfaces.events.transform.JobMaterialsCommandEventAssembler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Handles JobAddedToMachineEvent messages published by the Business Service.
 * When a new job event is received, it triggers material allocation in the inventory.
 */
@Configuration
public class JobAddedToMachineEventHandler {

    private final AllocateMaterialsCommandService allocateMaterialsCommandService;


    // Constructor to inject the service dependency
    /**
     * Provide the dependencies
     * @param allocateMaterialsCommandService
     */
    public JobAddedToMachineEventHandler(AllocateMaterialsCommandService allocateMaterialsCommandService) {
        this.allocateMaterialsCommandService = allocateMaterialsCommandService;
    }

    /**
     * Spring Cloud Stream consumer that listens for JobAddedToMachineEvent events from Kafka.
     * When the event is received, it processes the event to allocate materials.
     */
    @Bean
    public Consumer<JobAddedToMachineEvent> receiveNewJobEvent() {
        return jobAddedToMachineEvent -> {
            // Print event details for logging purposes
            System.out.println("--------------------------------------------------");
            System.out.println("[Stream] Received JobAddedToMachineEvent:");
            System.out.println("   → Machine ID: " + jobAddedToMachineEvent.getJobAddedToMachineEventData().getMachineId());
            System.out.println("   → Job Number: " + jobAddedToMachineEvent.getJobAddedToMachineEventData().getJobNumber());
            System.out.println("   → Material Needed: " + jobAddedToMachineEvent.getJobAddedToMachineEventData().getMaterialNeeded());
            System.out.println("   → Amount: " + jobAddedToMachineEvent.getJobAddedToMachineEventData().getMaterialAmount());
            System.out.println("   → Customer: " + jobAddedToMachineEvent.getJobAddedToMachineEventData().getCustomerName());
            System.out.println("--------------------------------------------------");

//            // Create command to allocate materials for the job
//            AddJobMaterialsCommand cmd = new AddJobMaterialsCommand(
//                    jobAddedToMachineEvent.getJobAddedToMachineEventData().getJobNumber(),
//                    jobAddedToMachineEvent.getJobAddedToMachineEventData().getMaterialNeeded(),
//                    jobAddedToMachineEvent.getJobAddedToMachineEventData().getMaterialAmount()
//            );

            // Trigger material allocation using the service
            allocateMaterialsCommandService.allocateMaterials(
                    JobMaterialsCommandEventAssembler.toCommandFromEvent(jobAddedToMachineEvent));

            // Log completion of material allocation
            System.out.println("[Stream] Material allocation completed for job: "
                    + jobAddedToMachineEvent.getJobAddedToMachineEventData().getJobNumber());
            System.out.println("--------------------------------------------------");
        };
    }
}
