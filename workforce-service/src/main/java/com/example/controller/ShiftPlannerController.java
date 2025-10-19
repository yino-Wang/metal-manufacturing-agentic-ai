package com.example.controller;

import com.example.domain.model.entities.ShiftPlan;
import com.example.service.ShiftPlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-planner")
public class ShiftPlannerController {

    private final ShiftPlannerService shiftPlannerService;

    public ShiftPlannerController(ShiftPlannerService shiftPlannerService) {
        this.shiftPlannerService = shiftPlannerService;
    }

    /**
     * Create shift plans using ExternalMachineSchedule's mock data
     */
    @PostMapping("/create-with-mock-data")
    public ResponseEntity<List<ShiftPlan>> createShiftPlansWithMockData(
            @RequestParam(defaultValue = "1") int requiredEmployees) {

        // Use the main method which already uses ExternalMachineSchedule's mock data
        List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlans(requiredEmployees);
        return ResponseEntity.ok(shiftPlans);
    }

    /**
     * Create shift plans from ExternalMachineSchedule (business-service integration)
     */
    @PostMapping("/create")
    public ResponseEntity<List<ShiftPlan>> createShiftPlans(
            @RequestParam(defaultValue = "2") int requiredEmployees) {

        List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlans(requiredEmployees);
        return ResponseEntity.ok(shiftPlans);
    }

    /**
     * Fetch machine schedule from ExternalMachineSchedule for testing
     */
    @GetMapping("/mock-schedule")
    public ResponseEntity<com.example.shared.MachineSchedule> getMockMachineSchedule() {
        // Use the existing method that fetches from ExternalMachineSchedule
        com.example.shared.MachineSchedule schedule = shiftPlannerService.fetchMachineScheduleFromBusiness();
        return ResponseEntity.ok(schedule);
    }
}
