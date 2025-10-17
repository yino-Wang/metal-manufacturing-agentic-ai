package com.example.controller;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.Payslip;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.*;
import com.example.service.DTO.AutoScheduleRequest;
import com.example.service.DTO.AutoScheduleResponse;
import com.example.service.usecase.GenerateShiftPlanService;
import com.example.service.usecase.RecordTimesheetService;
import com.example.service.queryservice.TimesheetQueryService;
import com.example.service.queryservice.ShiftPlanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workforce")
public class WorkforceController {
    @Autowired
    private TimesheetQueryService timesheetQueryService;
    @Autowired
    private ShiftPlanQueryService shiftPlanQueryService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimesheetRepository timesheetRepository;
    @Autowired
    private PayslipRepository payslipRepository;
    @Autowired
    private ShiftPlanRepository shiftPlanRepository;
    @Autowired
    private IndividualScheduleRepository individualScheduleRepository;
    @Autowired
    private TimesheetEventRepository timesheetEventRepository;
    @Autowired
    private RecordTimesheetService recordTimesheetService;
    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;


    // Employee Portal APIs
    @GetMapping("/portal/employee/{employeeId}/working-hours")
    public Map<String, Object> getEmployeeWorkingHours(
            @PathVariable Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Timesheet> timesheets = timesheetQueryService.findByEmployeeId(employeeId);

        // Calculate total and current period working hours
        double totalHours = timesheets.stream()
                .mapToDouble(Timesheet::getHoursWorked)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("totalHours", totalHours);
        response.put("currentMonthHours", calculateCurrentMonthHours(timesheets));
        return response;
    }

    @GetMapping("/portal/employee/{employeeId}/current-salary")
    public Map<String, Object> getCurrentSalary(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Get current month's payslip - using startDate for current month
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1); // First day of current month
        Date currentMonthStart = cal.getTime();
        List<Payslip> currentMonthPayslips = payslipRepository.findByEmployee_EmployeeIdAndStartDate(employeeId, currentMonthStart);

        Map<String, Object> response = new HashMap<>();
        response.put("baseSalary", employee.getSalary()); // Assuming base salary is stored in Employee entity
        response.put("currentMonthSalary", calculateCurrentMonthSalary(currentMonthPayslips));
        return response;
    }

    @GetMapping("/portal/employee/{employeeId}/schedule-summary")
    public Map<String, Object> getScheduleSummary(
            @PathVariable Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ShiftSchedule> schedules = shiftPlanQueryService.findByEmployeeId(employeeId.longValue());

        Map<String, Object> response = new HashMap<>();
        response.put("upcomingShifts", getUpcomingShifts(schedules));
        response.put("totalScheduledHours", calculateTotalScheduledHours(schedules));
        return response;
    }

    @GetMapping("/portal/employee/{employeeId}/payslip-summary")
    public Map<String, Object> getPayslipSummary(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Payslip> payslips = payslipRepository.findByEmployee_EmployeeId(employeeId);

        Map<String, Object> response = new HashMap<>();
        response.put("payslips", payslips);
        response.put("yearToDateEarnings", calculateYearToDateEarnings(payslips));
        return response;
    }

    // Clock In/Out and Timesheet Recording APIs  --todo
    @PostMapping("/portal/employee/{employeeId}/clock-in-out")
    public ResponseEntity<?> recordTimesheet(
            @PathVariable Long employeeId,
            @RequestParam Date date,
            @RequestParam Float hoursWorked,
            @RequestParam LocalDateTime clockInTime,
            @RequestParam LocalDateTime clockOutTime) {
        try {
            recordTimesheetService.recordTimesheet(employeeId, date, hoursWorked, clockInTime, clockOutTime);
            return ResponseEntity.ok("Timesheet recorded successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/portal/timesheet/{timesheetId}/approve")
    public ResponseEntity<?> approveTimesheet(@PathVariable Long timesheetId) {
        try {
            recordTimesheetService.approveTimesheet(timesheetId);
            return ResponseEntity.ok("Timesheet approved successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/portal/timesheet/{timesheetId}/reject")
    public ResponseEntity<?> rejectTimesheet(@PathVariable Long timesheetId) {
        try {
            recordTimesheetService.rejectTimesheet(timesheetId);
            return ResponseEntity.ok("Timesheet rejected successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Manager Portal APIs
    // 0. Auto-generate shift plan
    @PostMapping("/manager/portal/auto-schedule")
    public ResponseEntity<AutoScheduleResponse> autoSchedule(@RequestBody AutoScheduleRequest request) {
        try {
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                    request.getStartDate(), request.getEndDate(), request.getJobId(),
                    request.getRequiredEmployees(), request.getShiftType()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            //
            AutoScheduleResponse response = new AutoScheduleResponse();
            response.setAlternatives(generateShiftPlanService.recommendAlternativeEmployees(
                    request.getStartDate(), request.getShiftType(), 100f));
            return ResponseEntity.ok(response);
        }
    }
    // 1. Update shift plan (manager adjustment)
    @PutMapping("/manager/portal/shift-plan/{id}") //todo update path
    public ResponseEntity<?> updateShiftPlan(@PathVariable Long id, @RequestBody ShiftSchedule updatedData) {
        try {
            ShiftSchedule updated = generateShiftPlanService.updateShiftPlan(id, updatedData);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Approve shift plan (manager approval)
    @PutMapping("/manager/portal/shift-plan/{id}/approve")
    public ResponseEntity<?> approveShiftPlan(@PathVariable Long id) {
        try {
            ShiftSchedule approved = generateShiftPlanService.approveShiftPlan(id);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Validate shift plan compliance
    @GetMapping("/manager/portal/shift-plan/{id}/validate")
    public ResponseEntity<?> validateShiftPlan(@PathVariable Long id) {
        try {
            ShiftSchedule schedule = shiftPlanRepository.findById(id).orElseThrow();
            boolean isValid = generateShiftPlanService.validateCompliance(schedule.getEmployeeId(), schedule.getShiftDate());
            return ResponseEntity.ok(Map.of("compliance", isValid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Query shift plan version history
    @GetMapping("/manager/portal/shift-plan/{id}/versions")
    public ResponseEntity<?> getShiftPlanVersions(@PathVariable Long id) {
        try {
            ShiftSchedule schedule = shiftPlanRepository.findById(id).orElseThrow();
            List<ShiftSchedule> versions = shiftPlanRepository.findByEmployeeId(schedule.getEmployeeId());
            return ResponseEntity.ok(versions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Notify employee (after approval)
    @PostMapping("/manager/portal/shift-plan/{id}/notify")
    public ResponseEntity<?> notifyEmployee(@PathVariable Long id) {
        try {
            ShiftSchedule schedule = shiftPlanRepository.findById(id).orElseThrow();
            generateShiftPlanService.notifyEmployee(schedule);
            return ResponseEntity.ok("Notification sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. Get alternative employees for exception handling
    @GetMapping("/manager/portal/shift-plan/{id}/alternatives")
    public ResponseEntity<?> getAlternatives(@PathVariable Long id, @RequestParam String skill, @RequestParam Float maxCost) {
        try {
            ShiftSchedule schedule = shiftPlanRepository.findById(id).orElseThrow();
            List<Employee> alternatives = generateShiftPlanService.recommendAlternativeEmployees(schedule.getShiftDate(), skill, maxCost);
            return ResponseEntity.ok(alternatives);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper methods
    private double calculateCurrentMonthHours(List<Timesheet> timesheets) {
        Calendar cal = Calendar.getInstance();
        return timesheets.stream()
                .filter(t -> isCurrentMonth(t.getWorkDate()))
                .mapToDouble(Timesheet::getHoursWorked)
                .sum();
    }

    private double calculateCurrentMonthSalary(List<Payslip> payslips) {
        return payslips.stream()
                .mapToDouble(Payslip::getTotalSalary)
                .sum();
    }

    private List<ShiftSchedule> getUpcomingShifts(List<ShiftSchedule> schedules) {
        Date now = new Date();
        return schedules.stream()
                .filter(s -> s.getShiftDate().after(now))
                .sorted(Comparator.comparing(ShiftSchedule::getShiftDate))
                .limit(5)
                .collect(Collectors.toList());
    }

    private double calculateTotalScheduledHours(List<ShiftSchedule> schedules) {
        return schedules.stream()
                .mapToDouble(s -> calculateShiftDuration(s))
                .sum();
    }

    private double calculateYearToDateEarnings(List<Payslip> payslips) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return payslips.stream()
                .filter(p -> getYear(p.getEndDate()) == currentYear)
                .mapToDouble(Payslip::getTotalSalary)
                .sum();
    }

    private boolean isCurrentMonth(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    private int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private double calculateShiftDuration(ShiftSchedule schedule) {
        // Implement shift duration calculation based on your business logic
        return 8.0; // Default to 8 hours per shift
    }
}
