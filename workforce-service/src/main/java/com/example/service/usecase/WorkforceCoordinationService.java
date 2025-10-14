package com.example.service.usecase;

import com.example.domain.event.MachineScheduleCreated;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.entities.MachineJobMapping;
import com.example.infrastructure.repository.JobRepository;
import com.example.infrastructure.repository.MachineJobMappingRepository;
import com.example.service.DTO.AutoScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * WorkforceCoordinationService
 * Handles business logic for processing machine schedule events from Business MS
 * Coordinates between machine production schedules and workforce planning
 * This service RESPONDS to machine schedules by organizing workforce
 */
@Service
public class WorkforceCoordinationService {
    private static final Logger logger = LoggerFactory.getLogger(WorkforceCoordinationService.class);

    private final GenerateShiftPlanService generateShiftPlanService;
    private final JobRepository jobRepository;
    private final MachineJobMappingRepository machineJobMappingRepository;
    private final EmployeeNotificationService notificationService;

    // Machine type to job role mapping for common machines
    private static final Map<String, String> MACHINE_TYPE_JOB_MAPPING = new HashMap<>();
    static {
        MACHINE_TYPE_JOB_MAPPING.put("CNC", "CNC Machine Operator");
        MACHINE_TYPE_JOB_MAPPING.put("WELDING", "Welder");
        MACHINE_TYPE_JOB_MAPPING.put("ASSEMBLY", "Assembly Line Worker");
        MACHINE_TYPE_JOB_MAPPING.put("QUALITY", "Quality Inspector");
        MACHINE_TYPE_JOB_MAPPING.put("PACKAGING", "Packaging Operator");
    }

    @Autowired
    public WorkforceCoordinationService(GenerateShiftPlanService generateShiftPlanService,
                                 JobRepository jobRepository,
                                 MachineJobMappingRepository machineJobMappingRepository,
                                 EmployeeNotificationService notificationService) {
        this.generateShiftPlanService = generateShiftPlanService;
        this.jobRepository = jobRepository;
        this.machineJobMappingRepository = machineJobMappingRepository;
        this.notificationService = notificationService;
    }

    /**
     * Process machine schedule event from Business MS and generate corresponding workforce plan
     * This is the main entry point for cross-service coordination
     */
    public void processMachineScheduleEvent(MachineScheduleCreated event) {
        try {
            logger.info("Received machine schedule event from Business MS: {}", event.getScheduleId());

            // 1. Map machine/production line to job roles
            Long jobId = mapMachineToJobRole(event.getMachineId(), event.getProductionLine());
            if (jobId == null) {
                logger.warn("No job mapping found for machine: {} on production line: {}",
                           event.getMachineId(), event.getProductionLine());
                return;
            }

            // 2. Generate workforce plan based on machine schedule requirements
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                event.getStartTime(),
                event.getEndTime(),
                jobId,
                event.getRequiredEmployees(),
                event.getShiftType()
            );

            // 3. Process the generated workforce schedule
            if (response != null && !response.getShiftSchedules().isEmpty()) {
                logger.info("Successfully coordinated {} employees for machine schedule ID: {}",
                           response.getShiftSchedules().size(), event.getScheduleId());

                processWorkforceAssignment(response, event);
            } else {
                logger.warn("No workforce plan generated for machine schedule ID: {}", event.getScheduleId());
            }

        } catch (Exception e) {
            logger.error("Error coordinating workforce for machine schedule event: {}", event.getScheduleId(), e);
            throw new RuntimeException("Failed to coordinate workforce with machine schedule", e);
        }
    }

    /**
     * Map machine ID and production line to corresponding job roles
     * This is where business logic determines which job corresponds to which machine
     */
    private Long mapMachineToJobRole(String machineId, String productionLine) {
        logger.info("Mapping machine {} on production line {} to job role", machineId, productionLine);

        try {
            // 1. First try to find exact mapping in database
            Optional<MachineJobMapping> exactMapping = machineJobMappingRepository
                .findByMachineIdAndProductionLine(machineId, productionLine);

            if (exactMapping.isPresent()) {
                logger.info("Found exact mapping: Machine {} -> Job ID {}", machineId, exactMapping.get().getJobId());
                return exactMapping.get().getJobId();
            }

            // 2. Try to find mapping by machine ID only
            Optional<MachineJobMapping> machineMapping = machineJobMappingRepository
                .findByMachineIdAndIsActive(machineId, true);

            if (machineMapping.isPresent()) {
                logger.info("Found machine mapping: Machine {} -> Job ID {}", machineId, machineMapping.get().getJobId());
                return machineMapping.get().getJobId();
            }

            // 3. Try to infer job type from machine ID pattern
            String machineType = extractMachineType(machineId);
            if (machineType != null && MACHINE_TYPE_JOB_MAPPING.containsKey(machineType)) {
                String jobTitle = MACHINE_TYPE_JOB_MAPPING.get(machineType);
                Optional<Job> job = jobRepository.findByTitle(jobTitle);

                if (job.isPresent()) {
                    logger.info("Inferred job from machine type: {} -> Job ID {}", machineType, job.get().getJobId());

                    // Create mapping for future use
                    createMachineJobMapping(machineId, productionLine, job.get().getJobId(), "INTERMEDIATE");

                    return job.get().getJobId();
                }
            }

            // 4. Try to find job by production line
            if (productionLine != null) {
                Optional<Job> lineJob = findJobByProductionLine(productionLine);
                if (lineJob.isPresent()) {
                    logger.info("Found job by production line: {} -> Job ID {}", productionLine, lineJob.get().getJobId());

                    // Create mapping for future use
                    createMachineJobMapping(machineId, productionLine, lineJob.get().getJobId(), "BASIC");

                    return lineJob.get().getJobId();
                }
            }

            // 5. If all else fails, create a default job
            logger.warn("No existing mapping found, creating default job for machine: {}", machineId);
            return createDefaultJobForMachine(machineId, productionLine);

        } catch (Exception e) {
            logger.error("Error mapping machine {} to job role: {}", machineId, e.getMessage());
            return createDefaultJobForMachine(machineId, productionLine);
        }
    }

    /**
     * Extract machine type from machine ID (e.g., "CNC-001" -> "CNC")
     */
    private String extractMachineType(String machineId) {
        if (machineId == null || machineId.trim().isEmpty()) {
            return null;
        }

        String[] parts = machineId.split("-");
        if (parts.length > 0) {
            return parts[0].toUpperCase();
        }

        return machineId.toUpperCase();
    }

    /**
     * Find job by production line pattern
     */
    private Optional<Job> findJobByProductionLine(String productionLine) {
        // Try to match production line to common job patterns
        String lineUpper = productionLine.toUpperCase();

        if (lineUpper.contains("ASSEMBLY")) {
            return jobRepository.findByTitleContaining("Assembly");
        } else if (lineUpper.contains("WELDING")) {
            return jobRepository.findByTitleContaining("Weld");
        } else if (lineUpper.contains("QUALITY")) {
            return jobRepository.findByTitleContaining("Quality");
        } else if (lineUpper.contains("PACKAGING")) {
            return jobRepository.findByTitleContaining("Pack");
        }

        return Optional.empty();
    }

    /**
     * Create machine-job mapping for future use
     */
    private void createMachineJobMapping(String machineId, String productionLine, Long jobId, String skillLevel) {
        try {
            MachineJobMapping mapping = new MachineJobMapping(machineId, productionLine, jobId, skillLevel);
            machineJobMappingRepository.save(mapping);
            logger.info("Created new machine-job mapping: {} -> Job ID {}", machineId, jobId);
        } catch (Exception e) {
            logger.error("Failed to create machine-job mapping: {}", e.getMessage());
        }
    }

    /**
     * Create a default job role for a machine if no mapping exists
     */
    private Long createDefaultJobForMachine(String machineId, String productionLine) {
        try {
            Job defaultJob = new Job();
            defaultJob.setTitle("Machine Operation - " + machineId);
            // TODO: set other job properties as needed

            Job savedJob = jobRepository.save(defaultJob);
            logger.info("Created default job role ID: {} for machine: {} on line: {}",
                       savedJob.getJobId(), machineId, productionLine);
            return savedJob.getJobId();

        } catch (Exception e) {
            logger.error("Failed to create default job role for machine: {}", machineId, e);
            return null;
        }
    }

    /**
     * Process generated workforce assignments
     */
    private void processWorkforceAssignment(AutoScheduleResponse response, MachineScheduleCreated event) {
        logger.info("Processing workforce assignment for {} employees", response.getShiftSchedules().size());

        try {
            // 1. Send notifications to assigned employees
            notificationService.notifyMultipleEmployees(response.getShiftSchedules(), event);

            // 2. Update employee availability status
            updateEmployeeAvailabilityStatus(response.getShiftSchedules());

            // 3. Record shift planning history
            recordShiftPlanningHistory(response, event);

            // 4. Calculate and log workforce coordination metrics
            calculateWorkforceMetrics(response, event);

            // 5. Create machine-workforce association
            createMachineWorkforceAssociation(response, event);

            logger.info("Successfully processed workforce assignment for machine schedule ID: {}",
                       event.getScheduleId());

        } catch (Exception e) {
            logger.error("Error processing workforce assignment: {}", e.getMessage());
            // Handle partial failure - some notifications might have been sent
            handleWorkforceAssignmentError(response, event, e);
        }
    }

    /**
     * Update employee availability status based on shift assignments
     */
    private void updateEmployeeAvailabilityStatus(java.util.List<ShiftSchedule> schedules) {
        for (ShiftSchedule schedule : schedules) {
            try {
                logger.debug("Updating availability for employee {} on {}",
                           schedule.getEmployeeId(), schedule.getShiftDate());

                // TODO: Implement actual availability update logic
                // - Mark employee as unavailable during assigned shift hours
                // - Update employee's weekly/monthly work hour counts
                // - Check for overtime regulations compliance
                // - Update employee's skill usage statistics

                logger.debug("Updated availability status for employee: {}", schedule.getEmployeeId());

            } catch (Exception e) {
                logger.error("Failed to update availability for employee {}: {}",
                           schedule.getEmployeeId(), e.getMessage());
            }
        }
    }

    /**
     * Record shift planning history for audit and analysis
     */
    private void recordShiftPlanningHistory(AutoScheduleResponse response, MachineScheduleCreated event) {
        try {
            logger.info("Recording shift planning history for machine schedule: {}", event.getScheduleId());

            // TODO: Implement history recording
            // - Save planning decision details
            // - Record AI algorithm performance metrics
            // - Store original requirements vs actual assignments
            // - Track planning time and efficiency

            for (ShiftSchedule schedule : response.getShiftSchedules()) {
                logger.debug("Recording history: Employee {} assigned to {} shift on {}",
                           schedule.getEmployeeId(), schedule.getShiftType(), schedule.getShiftDate());
            }

            logger.info("Shift planning history recorded successfully");

        } catch (Exception e) {
            logger.error("Failed to record shift planning history: {}", e.getMessage());
        }
    }

    /**
     * Calculate workforce coordination metrics
     */
    private void calculateWorkforceMetrics(AutoScheduleResponse response, MachineScheduleCreated event) {
        try {
            int assignedEmployees = response.getShiftSchedules().size();
            int requestedEmployees = event.getRequiredEmployees();

            double fulfillmentRate = (double) assignedEmployees / requestedEmployees * 100;

            logger.info("Workforce Coordination Metrics:");
            logger.info("- Requested employees: {}", requestedEmployees);
            logger.info("- Assigned employees: {}", assignedEmployees);
            logger.info("- Fulfillment rate: {:.1f}%", fulfillmentRate);
            logger.info("- Machine: {}", event.getMachineId());
            logger.info("- Production line: {}", event.getProductionLine());

            // TODO: Store metrics in database for reporting and analysis
            // - Track fulfillment rates over time
            // - Monitor machine utilization vs workforce availability
            // - Calculate cost implications
            // - Generate efficiency reports

            if (fulfillmentRate < 100) {
                logger.warn("Workforce shortage detected: Only {}/{} employees assigned for machine {}",
                           assignedEmployees, requestedEmployees, event.getMachineId());
                // TODO: Trigger shortage alert or contingency planning
            }

        } catch (Exception e) {
            logger.error("Failed to calculate workforce metrics: {}", e.getMessage());
        }
    }

    /**
     * Create association between machine schedule and workforce assignments
     */
    private void createMachineWorkforceAssociation(AutoScheduleResponse response, MachineScheduleCreated event) {
        try {
            logger.info("Creating machine-workforce association for schedule: {}", event.getScheduleId());

            // TODO: Implement association storage
            // - Link machine schedule ID with shift schedule IDs
            // - Store coordination timestamp
            // - Track assignment source (AI algorithm used)
            // - Enable future queries like "who is working on machine X?"

            for (ShiftSchedule schedule : response.getShiftSchedules()) {
                logger.debug("Associating employee {} with machine schedule {}",
                           schedule.getEmployeeId(), event.getScheduleId());
            }

            logger.info("Machine-workforce association created successfully");

        } catch (Exception e) {
            logger.error("Failed to create machine-workforce association: {}", e.getMessage());
        }
    }

    /**
     * Handle errors during workforce assignment processing
     */
    private void handleWorkforceAssignmentError(AutoScheduleResponse response, MachineScheduleCreated event, Exception error) {
        logger.error("Handling workforce assignment error for machine schedule: {}", event.getScheduleId());

        try {
            // TODO: Implement error recovery strategies
            // - Retry failed operations
            // - Send alerts to workforce managers
            // - Create fallback assignments
            // - Log detailed error information for debugging

            // For now, just log the error details
            logger.error("Workforce assignment error details:", error);
            logger.error("Affected machine: {}, Production line: {}, Required employees: {}",
                       event.getMachineId(), event.getProductionLine(), event.getRequiredEmployees());

        } catch (Exception e) {
            logger.error("Failed to handle workforce assignment error: {}", e.getMessage());
        }
    }

    /**
     * Validate machine schedule requirements
     */
    public boolean validateMachineScheduleEvent(MachineScheduleCreated event) {
        if (event == null) return false;
        if (event.getScheduleId() == null) return false;
        if (event.getMachineId() == null || event.getMachineId().trim().isEmpty()) return false;
        if (event.getStartTime() == null || event.getEndTime() == null) return false;
        if (event.getStartTime().after(event.getEndTime())) return false;
        if (event.getRequiredEmployees() <= 0) return false;

        return true;
    }
}
