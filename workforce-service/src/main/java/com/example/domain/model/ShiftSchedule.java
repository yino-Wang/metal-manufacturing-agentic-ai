package com.example.domain.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * ShiftPlan entity class for employee shift scheduling.
 */
@Entity
@Table(name = "shift_plan")
public class ShiftSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "shift_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date shiftDate;

    @Column(name = "shift_type")
    private String shiftType;

    @Column(name = "required_employees")
    private int requiredEmployees;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "job", insertable = false, updatable = false)
    private Job job;

    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "status")
    private String status;

    @Column(name = "version")
    private Integer version;

    // 构造方法、getter、setter
    public ShiftSchedule() {}

    public ShiftSchedule(Integer employeeId, Date shiftDate, String shiftType, int requiredEmployees, Employee employee, Job job, Integer jobId, String status, Integer version) {
        this.employeeId = employeeId;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.requiredEmployees = requiredEmployees;
        this.employee = employee;
        this.job = job;
        this.jobId = jobId;
        this.status = status;
        this.version = version;
    }

    // ...getter and setter method...
    public Long getId() {
        return id;}

    public void setId(Long id) {
        this.id = id;}

    public Integer getEmployeeId() {
        return employeeId;}

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;}

    public Date getShiftDate() {
        return shiftDate;}

    public void setShiftDate(Date shiftDate) {
        this.shiftDate = shiftDate;}

    public String getShiftType() {
        return shiftType;}

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;}


    public Employee getEmployee() {
        return employee; }

    public void setEmployee(Employee employee) {
        this.employee = employee; }

    public Job getJob() {
        return job; }

    public void setJob(Job job) {
        this.job = job; }

    public Integer getJobId() {
        return jobId; }

    public void setJobId(Integer jobId) {
        this.jobId = jobId; }

    public String getStatus() {
        return status; }

    public void setStatus(String status) {
        this.status = status; }

    public int getRequiredEmployees() {
        return requiredEmployees;
    }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
