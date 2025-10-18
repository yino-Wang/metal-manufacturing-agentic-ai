package com.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class RecordTimesheetRequest {
    private Long employeeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date workDate;

    private Float hoursWorked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime clockInTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime clockOutTime;

    private Long jobId; // Optional job ID

    // Constructors
    public RecordTimesheetRequest() {}

    public RecordTimesheetRequest(Long employeeId, Date workDate, Float hoursWorked,
                                 LocalDateTime clockInTime, LocalDateTime clockOutTime, Long jobId) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.jobId = jobId;
    }

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }

    public Float getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Float hoursWorked) { this.hoursWorked = hoursWorked; }

    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }

    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
}
