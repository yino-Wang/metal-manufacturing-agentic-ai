package com.example.controller;

import com.example.controller.dto.AddEmployeeRequest;
import com.example.controller.dto.RecordTimesheetRequest;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.Payslip;
import com.example.domain.model.entities.ShiftPlan;
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
import org.springframework.http.HttpStatus;

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
    private TimesheetEventRepository timesheetEventRepository;
    @Autowired
    private RecordTimesheetService recordTimesheetService;
    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;


    // ==================== EMPLOYEE MANAGEMENT APIs ====================

    /**
     * Add a new employee to the system
     */
    @PostMapping("/employees")
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody AddEmployeeRequest request) {
        try {
            // Validate required fields
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Employee name is required"));
            }

            if (request.getPay() == null || request.getPay() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Valid pay rate is required"));
            }

            // Create new employee
            Employee employee = new Employee();
            employee.setName(request.getName().trim());
            employee.setPay(request.getPay());
            employee.setSkill(request.getSkill() != null ? request.getSkill() : "");
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setSalary(request.getSalary());
            employee.setManagementArea(request.getManagementArea());
            employee.setManagerName(request.getManagerName());
            employee.setManager(request.getManager() != null ? request.getManager() : false);

            // Save employee
            Employee savedEmployee = employeeRepository.save(employee);

            // Return success response with employee details
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Employee added successfully");
            response.put("employeeId", savedEmployee.getEmployeeId());
            response.put("employee", Map.of(
                "id", savedEmployee.getEmployeeId(),
                "name", savedEmployee.getName(),
                "pay", savedEmployee.getPay(),
                "skill", savedEmployee.getSkill()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to add employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all employees
     */
    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", employees.size());
            response.put("employees", employees.stream().map(emp -> Map.of(
                "id", emp.getEmployeeId(),
                "name", emp.getName(),
                "pay", emp.getPay(),
                "skill", emp.getSkill() != null ? emp.getSkill() : "",
                "phoneNumber", emp.getPhoneNumber() != null ? emp.getPhoneNumber() : "",
                "manager", emp.getManager() != null ? emp.getManager() : false
            )).collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to retrieve employees: " + e.getMessage()));
        }
    }

    /**
     * Delete an employee and all their timesheet records
     */
    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable Long employeeId) {
        try {
            // Verify employee exists
            if (!employeeRepository.existsById(employeeId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Employee not found with ID: " + employeeId));
            }

            // Get employee info before deletion for response
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            String employeeName = employee != null ? employee.getName() : "Unknown";

            // Count timesheets to be deleted
            List<Timesheet> employeeTimesheets = timesheetRepository.findByEmployee_EmployeeId(employeeId);
            int timesheetCount = employeeTimesheets.size();

            // Delete all timesheets for this employee first (to avoid foreign key constraints)
            timesheetRepository.deleteAll(employeeTimesheets);

            // Delete the employee
            employeeRepository.deleteById(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Employee and all associated timesheets deleted successfully");
            response.put("deletedEmployee", Map.of(
                "employeeId", employeeId,
                "name", employeeName
            ));
            response.put("deletedTimesheets", timesheetCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to delete employee: " + e.getMessage()));
        }
    }

    // ==================== TIMESHEET MANAGEMENT APIs ====================

    /**
     * Add/Record a new timesheet entry
     */
    @PostMapping("/timesheets")
    public ResponseEntity<Map<String, Object>> addTimesheet(@RequestBody RecordTimesheetRequest request) {
        try {
            // Validate required fields
            if (request.getEmployeeId() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Employee ID is required"));
            }

            if (request.getJobId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Job ID is required"));
            }

            if (request.getWorkDate() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Work date is required"));
            }

            if (request.getHoursWorked() == null || request.getHoursWorked() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Valid hours worked is required"));
            }

            if (request.getClockInTime() == null || request.getClockOutTime() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Clock in and clock out times are required"));
            }

            // Verify employee exists
            if (!employeeRepository.existsById(request.getEmployeeId())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Employee not found with ID: " + request.getEmployeeId()));
            }

            // Record timesheet
            recordTimesheetService.recordTimesheet(
                request.getEmployeeId(), request.getJobId(),
                request.getWorkDate(),
                request.getHoursWorked(),
                request.getClockInTime(),
                request.getClockOutTime()
            );

            // Get the created timesheet for response
            List<Timesheet> employeeTimesheets = timesheetRepository.findByEmployee_EmployeeId(request.getEmployeeId());
            Timesheet createdTimesheet = employeeTimesheets.stream()
                .filter(ts -> ts.getWorkDate().equals(request.getWorkDate()) &&
                             ts.getClockInTime().equals(request.getClockInTime()))
                .findFirst()
                .orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet recorded successfully");

            if (createdTimesheet != null) {
                response.put("timesheet", Map.of(
                    "timesheetId", createdTimesheet.getTimesheetId(),
                    "employeeId", createdTimesheet.getEmployeeId(),
                    "jobId", createdTimesheet.getJobId(),
                    "workDate", createdTimesheet.getWorkDate(),
                    "hoursWorked", createdTimesheet.getHoursWorked(),
                    "salaryPaid", createdTimesheet.getSalaryPaid(),
                    "status", createdTimesheet.getStatus(),
                    "clockInTime", createdTimesheet.getClockInTime(),
                    "clockOutTime", createdTimesheet.getClockOutTime()
                ));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to record timesheet: " + e.getMessage()));
        }
    }

    /**
     * Get all timesheets with optional filtering
     */
    @GetMapping("/timesheets")
    public ResponseEntity<Map<String, Object>> getAllTimesheets(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) {
        try {
            List<Timesheet> timesheets;

            if (employeeId != null) {
                timesheets = timesheetRepository.findByEmployee_EmployeeId(employeeId);
            } else {
                timesheets = timesheetRepository.findAll();
            }

            // Filter by status if provided
            if (status != null && !status.trim().isEmpty()) {
                timesheets = timesheets.stream()
                    .filter(ts -> status.equalsIgnoreCase(ts.getStatus()))
                    .collect(Collectors.toList());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", timesheets.size());
            response.put("timesheets", timesheets.stream().map(ts -> Map.of(
                "timesheetId", ts.getTimesheetId(),
                "employeeId", ts.getEmployeeId(),
                "jobId", ts.getJobId(),
                "workDate", ts.getWorkDate(),
                "hoursWorked", ts.getHoursWorked(),
                "salaryPaid", ts.getSalaryPaid() != null ? ts.getSalaryPaid() : 0.0f,
                "status", ts.getStatus() != null ? ts.getStatus() : "PENDING",
                "clockInTime", ts.getClockInTime(),
                "clockOutTime", ts.getClockOutTime()
            )).collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to retrieve timesheets: " + e.getMessage()));
        }
    }

    // ==================== EMPLOYEE PORTAL APIs  ====================
    //manager gets employee's workedhours
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
        List<Payslip> currentMonthPayslips = payslipRepository.findByEmployeeIdAndStartDate(employeeId, currentMonthStart);

        Map<String, Object> response = new HashMap<>();
        response.put("currentSalary", employee.getSalary()); // Assuming base salary is stored in Employee entity
  //      response.put("currentMonthSalary", calculateCurrentMonthSalary(currentMonthPayslips));
        return response;
    }

    @GetMapping("/portal/employee/{employeeId}/schedule-summary")
    public Map<String, Object> getScheduleSummary(
            @PathVariable Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ShiftPlan> schedules = shiftPlanQueryService.findByEmployeeId(employeeId.longValue());

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
        List<Payslip> payslips = payslipRepository.findByEmployeeId(employeeId);

        Map<String, Object> response = new HashMap<>();
        response.put("payslips", payslips);
        response.put("yearToDateEarnings", calculateYearToDateEarnings(payslips));
        return response;
    }

    /**
     * Clock In/Out - Improved version with proper DTO
     * @deprecated Use /timesheets endpoint instead for better API design
     */
    @PostMapping("/portal/employee/{employeeId}/clock-in-out")
    public ResponseEntity<Map<String, Object>> recordTimesheetLegacy(
            @PathVariable Long employeeId,
            @RequestBody RecordTimesheetRequest request) {
        try {
            // Override employeeId from path parameter for consistency
            request.setEmployeeId(employeeId);

            // Delegate to the new addTimesheet method
            return addTimesheet(request);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Failed to record timesheet: " + e.getMessage()));
        }
    }

    /**
     * Approve a timesheet entry
     */
    @PutMapping("/portal/timesheet/{timesheetId}/approve")
    public ResponseEntity<Map<String, Object>> approveTimesheet(@PathVariable Long timesheetId) {
        try {
            // Verify timesheet exists
            if (!timesheetRepository.existsById(timesheetId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Timesheet not found with ID: " + timesheetId));
            }

            recordTimesheetService.approveTimesheet(timesheetId);

            // Get updated timesheet for response
            Timesheet approvedTimesheet = timesheetRepository.findById(timesheetId).orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet approved successfully");

            if (approvedTimesheet != null) {
                response.put("timesheet", Map.of(
                    "timesheetId", approvedTimesheet.getTimesheetId(),
                    "employeeId", approvedTimesheet.getEmployeeId(),
                    "status", approvedTimesheet.getStatus(),
                    "workDate", approvedTimesheet.getWorkDate(),
                    "hoursWorked", approvedTimesheet.getHoursWorked(),
                    "salaryPaid", approvedTimesheet.getSalaryPaid()
                ));
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to approve timesheet: " + e.getMessage()));
        }
    }

    /**
     * Reject a timesheet entry
     */
    @PutMapping("/portal/timesheet/{timesheetId}/reject")
    public ResponseEntity<Map<String, Object>> rejectTimesheet(@PathVariable Long timesheetId) {
        try {
            // Verify timesheet exists
            if (!timesheetRepository.existsById(timesheetId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Timesheet not found with ID: " + timesheetId));
            }

            recordTimesheetService.rejectTimesheet(timesheetId);

            // Get updated timesheet for response
            Timesheet rejectedTimesheet = timesheetRepository.findById(timesheetId).orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet rejected successfully");

            if (rejectedTimesheet != null) {
                response.put("timesheet", Map.of(
                    "timesheetId", rejectedTimesheet.getTimesheetId(),
                    "employeeId", rejectedTimesheet.getEmployeeId(),
                    "status", rejectedTimesheet.getStatus(),
                    "workDate", rejectedTimesheet.getWorkDate(),
                    "hoursWorked", rejectedTimesheet.getHoursWorked(),
                    "salaryPaid", rejectedTimesheet.getSalaryPaid()
                ));
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to reject timesheet: " + e.getMessage()));
        }
    }

    // ==================== MANAGER PORTAL APIs ====================


    /**
     * Update shift plan
     */
    @PutMapping("/manager/portal/shift-plan/{id}")
    public ResponseEntity<Map<String, Object>> updateShiftPlan(@PathVariable Long id, @RequestBody ShiftPlan updatedData) {
        try {
            // Validate shift plan exists
            if (!shiftPlanRepository.existsById(id)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Shift plan not found with ID: " + id));
            }

            // Validate employee exists if provided
            if (updatedData.getEmployeeId() != null && !employeeRepository.existsById(updatedData.getEmployeeId())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Employee not found with ID: " + updatedData.getEmployeeId()));
            }

            ShiftPlan updated = generateShiftPlanService.updateShiftPlan(id, updatedData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shift plan updated successfully");
            response.put("shiftPlan", Map.of(
                "shiftPlanId", updated.getShiftPlanId(),
                "employeeId", updated.getEmployeeId(),
                "shiftDate", updated.getShiftDate(),
                "status", updated.getStatus(),
                "version", updated.getVersion(),
                "jobId", updated.getJobId(),
                "requiredEmployees", updated.getRequiredEmployees()
            ));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Failed to update shift plan: " + e.getMessage()));
        }
    }


    /**
     * Get all shift plans
     */
    // java
    @GetMapping("/manager/portal/shift-plans")
    public ResponseEntity<Map<String, Object>> getAllShiftPlans() {
        try {
            List<ShiftPlan> allShiftPlans = shiftPlanRepository.findAll();

            allShiftPlans.sort(Comparator.comparing(ShiftPlan::getShiftDate).reversed());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", allShiftPlans.size());
            response.put("shiftPlans", allShiftPlans.stream().map(sp -> Map.of(
                    "shiftPlanId", sp.getShiftPlanId(),
                    "employeeId", sp.getEmployeeId(),
                    "shiftDate", sp.getShiftDate(),
                    "status", sp.getStatus() != null ? sp.getStatus() : "PENDING",
                    "jobId", sp.getJobId() != null ? sp.getJobId() : 0L,
                    "requiredEmployees", sp.getRequiredEmployees() != 0 ? sp.getRequiredEmployees() : 1,
                    "version", sp.getVersion() != null ? sp.getVersion() : 1
            )).collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Failed to retrieve shift plans: " + e.getMessage()));
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

    private List<ShiftPlan> getUpcomingShifts(List<ShiftPlan> schedules) {
        Date now = new Date();
        return schedules.stream()
                .filter(s -> s.getShiftDate().after(now))
                .sorted(Comparator.comparing(ShiftPlan::getShiftDate))
                .limit(5)
                .collect(Collectors.toList());
    }

    private double calculateTotalScheduledHours(List<ShiftPlan> schedules) {
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

    private double calculateShiftDuration(ShiftPlan schedule) {
        // Implement shift duration calculation based on your business logic
        return 8.0; // Default to 8 hours per shift
    }
}
