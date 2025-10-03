package com.example.service.usecase;

import com.example.domain.event.TimesheetEvent;
import com.example.domain.model.Employee;
import com.example.domain.model.Timesheet;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.TimesheetRepository;
import com.example.infrastructure.repository.ShiftPlanRepository;
import com.example.infrastructure.repository.TimesheetEventRepository;
import com.example.domain.model.ShiftSchedule;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

public class RecordTimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftPlanRepository shiftPlanRepository;
    private final TimesheetEventRepository timesheetEventRepository;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public RecordTimesheetService(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository, ShiftPlanRepository shiftPlanRepository, TimesheetEventRepository timesheetEventRepository, RestTemplate restTemplate, ApplicationEventPublisher eventPublisher) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
        this.shiftPlanRepository = shiftPlanRepository;
        this.timesheetEventRepository = timesheetEventRepository;
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
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
        eventPublisher.publishEvent(timesheetEvent);
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
