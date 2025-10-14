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

            logger.info("Processing machine schedule - ID: {}, Machine: {}, Production Line: {}, " +
                       "Shift Type: {}, Required Employees: {}, Skills: {}",
                       event.getScheduleId(), event.getMachineId(), event.getProductionLine(),
                       event.getShiftType(), event.getRequiredEmployees(), event.getSkillRequirements());

            // Delegate to service layer for workforce coordination
            workforceCoordinationService.processMachineScheduleEvent(event);

            logger.info("Successfully coordinated workforce for machine schedule event: {}", event.getScheduleId());

        } catch (Exception e) {
            logger.error("Error processing machine schedule event: {}", event, e);
            handleProcessingError(event, e);
        }
    }

    /**
     * Handle errors in message processing
     */
    private void handleProcessingError(MachineScheduleCreated event, Exception error) {
        logger.error("Failed to process machine schedule event: {}", event, error);

        // TODO: 实现错误处理策略
        // 1. 记录失败事件到数据库
        // 2. 发送告警通知
        // 3. 重试机制
        // 4. 死信队列处理
    }
}
