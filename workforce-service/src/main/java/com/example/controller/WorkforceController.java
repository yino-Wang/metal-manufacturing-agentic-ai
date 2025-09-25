package com.example.controller;

import com.example.domain.event.TimesheetEvent;
import com.example.domain.model.*;
import com.example.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workforce")
public class WorkforceController {
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

    // Employee endpoints
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get employee by ID
    @GetMapping("/employees/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

   // Create new employee
    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    //update employee
    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setEmployeeId(id);
        return employeeRepository.save(employee);
    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }

    // Timesheet endpoints
    @GetMapping("/timesheets")
    public List<Timesheet> getAllTimesheets() {
        return timesheetRepository.findAll();
    }

    // Get timesheets by employee ID
    @GetMapping("/timesheets/employee/{employeeId}")
    public List<Timesheet> getTimesheetsByEmployee(@PathVariable Long employeeId) {
        return timesheetRepository.findByEmployee_EmployeeId(employeeId);
    }

    // Payslip endpoints
    @GetMapping("/payslips")
    public List<Payslip> getAllPayslips() {
        return payslipRepository.findAll();
    }

    // Get payslips by employee ID
    @GetMapping("/payslips/employee/{employeeId}")
    public List<Payslip> getPayslipsByEmployee(@PathVariable Long employeeId) {
        return payslipRepository.findByEmployee_EmployeeId(employeeId);
    }

    // ShiftPlan endpoints
    @GetMapping("/shift-plans")
    public List<ShiftPlan> getAllShiftPlans() {
        return shiftPlanRepository.findAll();
    }

    // Get shift plans by employee ID
    @GetMapping("/shift-plans/employee/{employeeId}")
    public List<ShiftPlan> getShiftPlansByEmployee(@PathVariable Long employeeId) {
        return shiftPlanRepository.findByEmployeeId(employeeId.intValue());
    }

    // IndividualSchedule endpoints
    @GetMapping("/individual-schedules")
    public List<IndividualSchedule> getAllIndividualSchedules() {
        return individualScheduleRepository.findAll();
    }

    // Get schedules by assigned employee ID
    @GetMapping("/individual-schedules/employee/{employeeId}")
    public List<IndividualSchedule> getSchedulesByEmployee(@PathVariable Long employeeId) {
        return individualScheduleRepository.findByAssignedEmployee_EmployeeId(employeeId);
    }

    // TimesheetEvent endpoints
    @GetMapping("/timesheet-events")
    public List<TimesheetEvent> getAllTimesheetEvents() {
        return timesheetEventRepository.findAll();
    }
}
