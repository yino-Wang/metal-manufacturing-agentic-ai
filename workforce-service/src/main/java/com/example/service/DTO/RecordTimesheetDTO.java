package com.example.service.DTO;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for recording timesheet (API request/response)
 */
public class RecordTimesheetDTO {
    private Long employeeId;
    private Date workDate;
    private Float hoursWorked;
    private Long jobId;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    // getters and setters
    public Long getEmployeeId() {
        return employeeId; }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId; }

    public Date getWorkDate() {
        return workDate; }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate; }

    public Float getHoursWorked() {
        return hoursWorked; }

    public void setHoursWorked(Float hoursWorked) {
        this.hoursWorked = hoursWorked; }

    public Long getJobId() {
        return jobId; }

    public void setJobId(Long jobId) {
        this.jobId = jobId; }

    public LocalDateTime getClockInTime() {
        return clockInTime; }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime; }

    public LocalDateTime getClockOutTime() {
        return clockOutTime; }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime; }
}