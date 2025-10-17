package com.example.infrastructure.messaging;

import com.example.domain.event.MachineScheduleCreated;
import com.example.service.usecase.WorkforceCoordinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

/**
 * KafkaScheduleConsumer
 * Consumes machine schedule events from Business Management service via Kafka
 * Triggers workforce coordination and Agentic AI shift planning process
 */
@Component
public class KafkaScheduleConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaScheduleConsumer.class);

    private final WorkforceCoordinationService workforceCoordinationService;

    @Autowired
    public KafkaScheduleConsumer(WorkforceCoordinationService workforceCoordinationService) {
        this.workforceCoordinationService = workforceCoordinationService;
    }

    /**
     * Kafka consumer function for machine schedule events
     * Channel name: machineScheduleCreated-in-0
     */
    @Bean
    public Consumer<MachineScheduleCreated> machineScheduleCreated() {
        return this::handleMachineScheduleCreated;
    }

    /**
     * Handle machine schedule created event from Business MS
     * This is the entry point for cross-microservice coordination
     */
    private void handleMachineScheduleCreated(MachineScheduleCreated event) {
        try {
            logger.info("Received machine schedule event from Business MS: {}", event);

            // Validate event data
            if (!workforceCoordinationService.validateMachineScheduleEvent(event)) {
                logger.error("Invalid machine schedule event received: {}", event);
                return;
            }

            logger.info("Processing machine schedule - ID: {}, Machine: {}, Job ID: {}, Priority: {}, " +
                       "Start: {}, End: {}, Required Employees: {}, Skills: {}",
                       event.getScheduleId(), event.getMachineId(), event.getJobId(), event.getPriority(),
                       event.getStartTime(), event.getEndTime(), event.getRequiredEmployees(), event.getSkillRequirements());

            // Process the machine schedule event
            workforceCoordinationService.processMachineScheduleEvent(event);

            logger.info("Successfully processed machine schedule event: {}", event.getScheduleId());

        } catch (Exception e) {
            logger.error("Error processing machine schedule event: {}", event.getScheduleId(), e);
            handleProcessingError(event, e);
        }
    }

    /**
     * Handle processing errors
     */
    private void handleProcessingError(MachineScheduleCreated event, Exception error) {
        try {
            logger.error("Processing failed for machine schedule: {}", event.getScheduleId());
            logger.error("Error details: Machine: {}, Job ID: {}, Priority: {}, Error: {}",
                       event.getMachineId(), event.getJobId(), event.getPriority(), error.getMessage());

            // TODO: Implement error recovery strategies
            // - Store failed event for retry
            // - Send alert to operations team
            // - Try alternative workforce assignment strategies

        } catch (Exception e) {
            logger.error("Failed to handle processing error: {}", e.getMessage());
        }
    }
}
