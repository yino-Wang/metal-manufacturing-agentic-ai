package com.example.interfaces.events;

import com.example.application.AllocateMaterialsCommandService;
import com.example.events.JobAddedToMachineEvent;
import com.example.interfaces.events.transform.JobMaterialsCommandEventAssembler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Event Handler for the Cargo Routed Event that the Tracking Bounded Context is interested in
 */
@Configuration
public class JobAddedToMachineEventHandler {

        private AllocateMaterialsCommandService allocateMaterialsCommandService; // Application Service Dependency

        /**
         * Provide the dependencies
         *
         * @param allocateMaterialsCommandService
         */
        public JobAddedToMachineEventHandler(AllocateMaterialsCommandService allocateMaterialsCommandService) {
            this.allocateMaterialsCommandService = allocateMaterialsCommandService;
        }

        @Bean
        public Consumer<JobAddedToMachineEvent> receiveJobAddedEvent() {
            return JobAddedToMachineEvent -> {
                System.out.println("Job added to machine event" + JobAddedToMachineEvent);
                System.out.println(JobAddedToMachineEvent.getJobAddedToMachineEventData());
                System.out.println(JobAddedToMachineEvent.getJobAddedToMachineEventData().toString());
                //Process the Event
                allocateMaterialsCommandService.allocateMaterials(
                        JobMaterialsCommandEventAssembler
                                .toCommandFromEvent(JobAddedToMachineEvent));
            };
        }


}
