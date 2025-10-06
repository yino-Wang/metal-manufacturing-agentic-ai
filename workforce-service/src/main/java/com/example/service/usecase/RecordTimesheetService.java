package com.example.service.usecase;

import com.example.domain.event.TimesheetEvent;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.TimesheetRepository;
import com.example.infrastructure.repository.ShiftPlanRepository;
import com.example.infrastructure.repository.TimesheetEventRepository;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.commands.RecordTimesheetCommand;
import com.example.infrastructure.messaging.TimesheetEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class RecordTimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftPlanRepository shiftPlanRepository;
    private final TimesheetEventRepository timesheetEventRepository;
    private final TimesheetEventPublisher timesheetEventPublisher;

    public RecordTimesheetService(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository, ShiftPlanRepository shiftPlanRepository, TimesheetEventRepository timesheetEventRepository, TimesheetEventPublisher timesheetEventPublisher) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
        this.shiftPlanRepository = shiftPlanRepository;
        this.timesheetEventRepository = timesheetEventRepository;
        this.timesheetEventPublisher = timesheetEventPublisher;
    }

    public void recordTimesheet(Long employeeId, Date date, Float hoursWorked, LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        // Business logic to record timesheet
        // 1. Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        // 2. Create and save timesheet entry
        Timesheet timesheet = new Timesheet();
        timesheet.setEmployeeId(employeeId);
        timesheet.setWorkDate(date);
        timesheet.setClockInTime(clockInTime);
        timesheet.setClockOutTime(clockOutTime);
        timesheet.setHoursWorked(hoursWorked);

        // cal and record salaryPaid
        Float payRate = employee.getPay(); // salary per hour
        Float salaryPaid = payRate != null ? payRate * hoursWorked : 0f;
        timesheet.setSalaryPaid(salaryPaid);

        // determine status based on shift plan
        ShiftSchedule shiftSchedule = shiftPlanRepository.findAll().stream()
            .filter(sp -> sp.getEmployeeId().equals(employeeId) && sp.getShiftDate().equals(date))
            .findFirst().orElse(null);
        if (shiftSchedule == null) {
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
        ShiftSchedule shiftSchedule = shiftPlanRepository.findAll().stream()
            .filter(sp -> sp.getEmployeeId().equals(command.getEmployeeId()) && sp.getShiftDate().equals(command.getWorkDate()))
            .findFirst().orElse(null);
        if (shiftSchedule == null) {
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
