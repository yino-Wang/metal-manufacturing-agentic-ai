package com.example.interfaces.events;

import com.example.application.AllocateMaterialsCommandService;
import com.example.events.JobAddedToMachineEvent;
import com.example.interfaces.events.transform.JobMaterialsCommandEventAssembler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Stream processor for the "jobAddedToMachines" Kafka topic.
 * When a job event arrives, it allocates the required materials.
 */
@Configuration
public class JobAddedToMachineEventHandler {

    private final AllocateMaterialsCommandService allocateMaterialsCommandService;

    // Constructor-based dependency injection
    public JobAddedToMachineEventHandler(AllocateMaterialsCommandService allocateMaterialsCommandService) {
        this.allocateMaterialsCommandService = allocateMaterialsCommandService;
    }

    /**
     * Spring Cloud Stream function bean.
     * Triggered automatically whenever a JobAddedToMachineEvent
     * message is received from Kafka.
     */
    @Bean
    public Consumer<JobAddedToMachineEvent> handleJobEvents() {
        return event -> {
            System.out.println("[Stream] Received jobAddedToMachines event:");
            System.out.println("   → Job number: " + event.getJobAddedToMachineEventData().getJobNumber());
            System.out.println("   → Material: " + event.getJobAddedToMachineEventData().getMaterialNeeded());
            System.out.println("   → Amount: " + event.getJobAddedToMachineEventData().getMaterialAmount());

            // Allocate the materials for this job
            allocateMaterialsCommandService.allocateMaterials(
                    JobMaterialsCommandEventAssembler.toCommandFromEvent(event)
            );

            System.out.println("[Stream] Materials allocated for job: "
                    + event.getJobAddedToMachineEventData().getJobNumber());
        };
    }
}
