package com.example.controller;

import com.example.domain.model.entities.ShiftPlan;
import com.example.service.ShiftPlannerService;
import com.example.ExternalMachineSchedule;
import com.example.service.usecase.EmployeeNotificationService;
import com.example.shared.MachineSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-planner")
public class ShiftPlannerController {
    private static final Logger logger = LoggerFactory.getLogger(ShiftPlannerController.class);

    private final ShiftPlannerService shiftPlannerService;
    private final ExternalMachineSchedule externalMachineSchedule;
    private final EmployeeNotificationService employeeNotificationService;

    public ShiftPlannerController(ShiftPlannerService shiftPlannerService,
                                  ExternalMachineSchedule externalMachineSchedule, EmployeeNotificationService employeeNotificationService) {
        this.shiftPlannerService = shiftPlannerService;
        this.externalMachineSchedule = externalMachineSchedule;
        this.employeeNotificationService = employeeNotificationService;
    }

    /**
     * Create shift plans using ExternalMachineSchedule's mock data
     */
// java
    @PostMapping("/create-with-mock-data")
    public ResponseEntity<?> createShiftPlansWithMockData(
            @RequestParam(defaultValue = "1") int requiredEmployees) {

        logger.info("POST /create-with-mock-data requiredEmployees={}", requiredEmployees);

        if (requiredEmployees <= 0) {
            logger.warn("Invalid requiredEmployees: {}", requiredEmployees);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", "requiredEmployees must be > 0"));
        }

        MachineSchedule mockSchedule;
        try {
            mockSchedule = externalMachineSchedule.createMockMachineSchedule();
        } catch (Exception e) {
            logger.error("externalMachineSchedule.createMockMachineSchedule threw exception", e);
            return ResponseEntity.status(502)
                    .body(Map.of("error", "external_error", "message", "failed to obtain mock schedule"));
        }

        if (mockSchedule == null) {
            logger.error("Mock MachineSchedule is null");
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "mock schedule is null"));
        }

        logger.debug("Mock schedule received: {}", mockSchedule);

        List<ShiftPlan> shiftPlans;
        try {
            shiftPlans = shiftPlannerService.createShiftPlansFromSchedule(mockSchedule, requiredEmployees);
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error while creating shift plans", iae);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", iae.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to create shift plans from mock data", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "failed to generate shift plans"));
        }

        try {
            int notified = employeeNotificationService.notifyMultipleEmployees(shiftPlans, null);
            logger.info("Notified {} employees for mock-created shift plans", notified);
        } catch (Exception e) {
            logger.error("Failed to notify employees after creating mock shift plans: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(shiftPlans);
    }


    /**
     * Create shift plans from ExternalMachineSchedule (business-service integration)
     */
/*    @PostMapping("/create")
    public ResponseEntity<?> createShiftPlans(
            @RequestParam(defaultValue = "2") int requiredEmployees) {

        logger.info("POST /create-with-machineSchedule-data-from-business-MS requiredEmployees={}", requiredEmployees);

        if (requiredEmployees <= 0) {
            logger.warn("Invalid requiredEmployees: {}", requiredEmployees);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", "requiredEmployees must be > 0"));
        }

        MachineSchedule machineSchedule;
        try {
            machineSchedule = externalMachineSchedule.fetchNewSchedule();
        } catch (Exception e) {
            logger.error("externalMachineSchedule.fetchNewSchedule threw exception", e);
            return ResponseEntity.status(502)
                    .body(Map.of("error", "external_error", "message", "failed to obtain mock schedule"));
        }

        if (machineSchedule == null) {
            logger.error("MachineSchedule is null, fetchNewSchedule failed");
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "mock schedule is null"));
        }

        logger.debug("Schedule received: {}", machineSchedule);

        List<ShiftPlan> shiftPlans;
        try {
            shiftPlans = shiftPlannerService.createShiftPlansFromSchedule(machineSchedule, requiredEmployees);
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error while creating shift plans", iae);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", iae.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to create shift plans from business MS", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "failed to generate shift plans"));
        }

        try {
            int notified = employeeNotificationService.notifyMultipleEmployees(shiftPlans, null);
            logger.info("Notified {} employees for shift plans", notified);
        } catch (Exception e) {
            logger.error("Failed to notify employees after creating  shift plans: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(shiftPlans);
    }*/

    /**
     * Fetch machine schedule from ExternalMachineSchedule for testing
     */
    @GetMapping("/mock-schedule")
    public ResponseEntity<MachineSchedule> getMockMachineSchedule() {
        // Use the existing method that fetches from ExternalMachineSchedule
        MachineSchedule schedule = shiftPlannerService.fetchMachineScheduleFromBusiness();
        return ResponseEntity.ok(schedule);
    }

    /**
     * get ShiftPlans created from REST API data
     */
    @GetMapping("/api-schedule")
    public ResponseEntity<?> getApiScheduleData() {
        logger.info("GET /api-schedule - Fetching data from REST API and converting to ShiftPlan format");

        try {

            MachineSchedule machineSchedule = externalMachineSchedule.fetchNewSchedule();

            if (machineSchedule == null) {
                logger.error("Failed to fetch schedule from REST API");
                return ResponseEntity.status(500)
                        .body(Map.of("error", "api_error", "message", "Failed to fetch schedule from REST API"));
            }

            logger.info("Successfully fetched and converted API data to MachineSchedule format");
            logger.debug("Converted schedule: {}", machineSchedule);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully fetched and converted REST API data",
                    "schedules", machineSchedule.getSchedules()
            ));

        } catch (Exception e) {
            logger.error("Error fetching and converting API schedule data: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "Error processing API data: " + e.getMessage()));
        }
    }

    /**
     * create ShiftPlans from REST API data
     */
    @PostMapping("/create-from-api")
    public ResponseEntity<?> createShiftPlansFromApi(
            @RequestParam(defaultValue = "1") int requiredEmployees,
            @RequestParam(defaultValue = "MACHINE-001") String machineId) {

        logger.info("POST /create-from-api - Creating shift plans from REST API data, requiredEmployees={}", requiredEmployees);

        if (requiredEmployees <= 0) {
            logger.warn("Invalid requiredEmployees: {}", requiredEmployees);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", "requiredEmployees must be > 0"));
        }

        try {
            // fetch data from REST API
            MachineSchedule machineSchedule = externalMachineSchedule.fetchNewSchedule();

            if (machineSchedule == null) {
                logger.error("Failed to fetch schedule from REST API");
                return ResponseEntity.status(500)
                        .body(Map.of("error", "api_error", "message", "Failed to fetch schedule from REST API"));
            }

            logger.info("Successfully fetched schedule from REST API with {} machines",
                    machineSchedule.getSchedules().size());

            // create
            List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlansFromSchedule(machineSchedule, requiredEmployees);

            logger.info("Successfully created {} shift plans from API data", shiftPlans.size());

            // notify employees
            try {
                int notified = employeeNotificationService.notifyMultipleEmployees(shiftPlans, null);
                logger.info("Notified {} employees for API-based shift plans", notified);
            } catch (Exception e) {
                logger.error("Failed to notify employees after creating API-based shift plans: {}", e.getMessage(), e);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully created shift plans from REST API data",
                    "shiftPlans", shiftPlans,
                    "totalPlans", shiftPlans.size()
            ));

        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error while creating shift plans from API data", iae);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_request", "message", iae.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to create shift plans from API data: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "internal_error", "message", "Failed to generate shift plans: " + e.getMessage()));
        }
    }

}
