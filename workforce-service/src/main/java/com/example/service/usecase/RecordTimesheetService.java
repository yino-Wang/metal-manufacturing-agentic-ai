package com.example.service.usecase;

import com.example.domain.event.TimesheetEvent;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.Payslip;
import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.*;
import com.example.domain.model.entities.ShiftPlan;
import com.example.domain.model.commands.RecordTimesheetCommand;
import com.example.infrastructure.messaging.TimesheetEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class RecordTimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftPlanRepository shiftPlanRepository;
    private final TimesheetEventRepository timesheetEventRepository;
    private final TimesheetEventPublisher timesheetEventPublisher;
    private final PayslipRepository payslipRepository;


    public RecordTimesheetService(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository, ShiftPlanRepository shiftPlanRepository, TimesheetEventRepository timesheetEventRepository, TimesheetEventPublisher timesheetEventPublisher, PayslipRepository payslipRepository) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
        this.shiftPlanRepository = shiftPlanRepository;
        this.timesheetEventRepository = timesheetEventRepository;
        this.timesheetEventPublisher = timesheetEventPublisher;
        this.payslipRepository = payslipRepository;
    }

    public void recordTimesheet(Long employeeId, Long jobId, Date date, Float hoursWorked, LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        // Business logic to record timesheet
        // 1. Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        // 2. Create and save timesheet entry
        Timesheet timesheet = new Timesheet();
        timesheet.setEmployeeId(employeeId);
        timesheet.setJobId(jobId);
        timesheet.setWorkDate(date);
        timesheet.setClockInTime(clockInTime);
        timesheet.setClockOutTime(clockOutTime);
        timesheet.setHoursWorked(hoursWorked);

        // cal and record salaryPaid
        Float payRate = employee.getPay(); // salary per hour
        Float salaryPaid = payRate != null ? payRate * hoursWorked : 0f;
        timesheet.setSalaryPaid(salaryPaid);

        employee.setSalary(employee.getSalary() + salaryPaid);
        // determine status based on shift plan
        ShiftPlan shiftPlan = shiftPlanRepository.findAll().stream()
            .filter(sp -> sp.getEmployeeId().equals(employeeId) && sp.getShiftDate().equals(date))
            .findFirst().orElse(null);
        if (shiftPlan == null) {
            timesheet.setStatus("EXCEPTION"); // no shift plan found
        } else {
            // normal case
            timesheet.setStatus("NORMAL");
        }
        timesheetRepository.save(timesheet);

        // 3. Publish event
        TimesheetEvent timesheetEvent = new TimesheetEvent(timesheet);
        timesheetEventRepository.save(timesheetEvent);
        timesheetEventPublisher.publish(timesheetEvent);

        //record this payslip to employee
        Payslip payslip = new Payslip();
        payslip.setEmployeeId(employeeId);
        payslip.setStartDate(date); // for simplicity, set startDate as workDate
        payslip.setEndDate(date); // for simplicity, set endDate as workDate
        payslip.setThisPay(salaryPaid);
        payslip.setTotalSalary(employee.getSalary());
        payslipRepository.save(payslip);
        System.out.println("Payslip recorded!");

    }

    /**
     * Record a timesheet using a command object (recommended for API integration)
     */
    public void recordTimesheet(RecordTimesheetCommand command) {
        Employee employee = employeeRepository.findById(command.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Timesheet timesheet = new Timesheet();
        timesheet.setEmployeeId(command.getEmployeeId());
        timesheet.setWorkDate(command.getWorkDate());
        timesheet.setClockInTime(command.getClockInTime());
        timesheet.setClockOutTime(command.getClockOutTime());
        timesheet.setHoursWorked(command.getHoursWorked());
        timesheet.setJobId(command.getJobId());
        Float payRate = employee.getPay();
        Float salaryPaid = payRate != null ? payRate * command.getHoursWorked() : 0f;
        timesheet.setSalaryPaid(salaryPaid);
        ShiftPlan shiftPlan = shiftPlanRepository.findAll().stream()
            .filter(sp -> sp.getEmployeeId().equals(command.getEmployeeId()) && sp.getShiftDate().equals(command.getWorkDate()))
            .findFirst().orElse(null);
        if (shiftPlan == null) {
            timesheet.setStatus("EXCEPTION");
        } else {
            timesheet.setStatus("NORMAL");
        }
        timesheetRepository.save(timesheet);
        TimesheetEvent timesheetEvent = new TimesheetEvent(timesheet);
        timesheetEventRepository.save(timesheetEvent);
        timesheetEventPublisher.publish(timesheetEvent);
    }

    // approve timesheet exception
    public void approveTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId).orElseThrow();
        timesheet.setStatus("APPROVED");
        timesheetRepository.save(timesheet);
    }
    // reject timesheet exception
    public void rejectTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId).orElseThrow();
        timesheet.setStatus("REJECTED");
        timesheetRepository.save(timesheet);
    }

}
