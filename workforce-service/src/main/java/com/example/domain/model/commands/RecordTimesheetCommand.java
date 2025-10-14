package com.example.domain.model.commands;

import java.util.Date;
import java.time.LocalDateTime;

/**
 * Command for recording timesheet.
 * Encapsulates all data required for the RecordTimesheet use case.
 */
public class RecordTimesheetCommand {
    private Long employeeId;
    private Date workDate;
    private Float hoursWorked;
    private Long jobId;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    public RecordTimesheetCommand(Long employeeId, Date workDate, Float hoursWorked, Long jobId, LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.jobId = jobId;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }

    public Float getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Float hoursWorked) { this.hoursWorked = hoursWorked; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }

    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }
}
