package com.example.domain.model.entities;

import com.example.domain.model.aggregates.Employee;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Timesheet entity class for recording employee work hours and salary.
 */
@Entity
@Table(name = "timesheet")
public class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timesheetId;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "work_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date workDate;

    @Column(name = "clock_in_time")
    private LocalDateTime ClockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime ClockOutTime;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "hours_worked")
    private Float hoursWorked = ClockOutTime != null && ClockInTime != null ?
            (float) java.time.Duration.between(ClockInTime, ClockOutTime).toHours() : 0;

    @Column(name = "salary_paid")
    private Float salaryPaid;

    @Column(name = "status")
    private String status; // 工时状态：NORMAL/EXCEPTION/PENDING_APPROVAL/APPROVED/REJECTED

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "payslip_id")
    private Payslip payslip;

    @Column(name = "payslip_id", insertable = false, updatable = false)
    private Long payslipId;

    // 构造方法、getter、setter
    public Timesheet() {}

    public Timesheet(Long timesheetId, Long employeeId, Date workDate, LocalDateTime clockInTime, LocalDateTime clockOutTime, Float salaryPaid, Long jobId, String status) {
        this.timesheetId = timesheetId;
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.ClockInTime = clockInTime;
        this.ClockOutTime = clockOutTime;
        this.hoursWorked = clockOutTime != null && clockInTime != null ?
            (float) java.time.Duration.between(clockInTime, clockOutTime).toHours() : 0;
        this.salaryPaid = salaryPaid;
        this.jobId = jobId;
        this.status = status;
    }

    // ...getter和setter方法...
    public Long getTimesheetId() {

        return timesheetId;
    }

    public void setTimesheetId(Long id)
    {
        this.timesheetId = timesheetId;
    }
    public Long getEmployeeId() {

        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;}

    public Date getWorkDate() {
        return workDate;}

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;}

    public Float getHoursWorked() {
        return hoursWorked;}

    public void setHoursWorked(Float hoursWorked) {
        this.hoursWorked = hoursWorked;}

    public Float getSalaryPaid() {
        return salaryPaid;}

    public void setSalaryPaid(Float salaryPaid) {
        this.salaryPaid = salaryPaid;}

    public LocalDateTime getClockInTime() {
        return ClockInTime;}

    public void setClockInTime(LocalDateTime clockInTime) {
        ClockInTime = clockInTime;}

    public LocalDateTime getClockOutTime() {
        return ClockOutTime;}

    public void setClockOutTime(LocalDateTime clockOutTime) {
        ClockOutTime = clockOutTime;}

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public Employee getEmployee() {
        return employee; }

    public void setEmployee(Employee employee) {
        this.employee = employee; }

    public Payslip getPayslip() {
        return payslip; }

    public void setPayslip(Payslip payslip) {
        this.payslip = payslip; }

    public Long getPayslipId() {
        return payslipId; }

    public void setPayslipId(Long payslipId) {
        this.payslipId = payslipId;
    }
    public Long getJobId() {
        return jobId; }

    public void setJobId(Long jobId) {
        this.jobId = jobId;}
}
