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
     * using mock data to create shift plans for testing
     */
    @PostMapping("/create-with-mock-data")
    public ResponseEntity<List<ShiftPlan>> createShiftPlansWithMockData(
            @RequestParam(defaultValue = "1") int requiredEmployees) {

        List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlansWithMockData(requiredEmployees);
        return ResponseEntity.ok(shiftPlans);
    }

    /**
     * try to create shift plans from business-service machine schedule, if fails, fallback to mock data
     */
    @PostMapping("/create")
    public ResponseEntity<List<ShiftPlan>> createShiftPlans(
            @RequestParam(defaultValue = "2") int requiredEmployees) {

        List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlans(requiredEmployees);
        return ResponseEntity.ok(shiftPlans);
    }

    /**
     * fetch mock machine schedule for testing
     */
    @GetMapping("/mock-schedule")
    public ResponseEntity<com.example.shared.MachineSchedule> getMockMachineSchedule() {
        com.example.shared.MachineSchedule mockSchedule = shiftPlannerService.createMockMachineSchedule();
        return ResponseEntity.ok(mockSchedule);
    }
}
