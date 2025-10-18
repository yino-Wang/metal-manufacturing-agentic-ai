package com.example.domain.model.entities;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * ShiftPlan entity class for employee shift scheduling.
 */
@Entity
@Table(name = "shift_plan")
public class ShiftPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "shift_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date shiftDate;

    @Column(name = "job_priority")
    private Integer jobPriority; // Add job priority field

    @Column(name = "required_employees")
    private int requiredEmployees;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "job", insertable = false, updatable = false)
    private Job job;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "status")
    private String status;

    @Column(name = "version")
    private Integer version;

    // Constructors
    public ShiftPlan() {}

    public ShiftPlan(Long employeeId, Date shiftDate, Integer jobPriority, int requiredEmployees, Employee employee, Job job, Long jobId, String status, Integer version) {
        this.employeeId = employeeId;
        this.shiftDate = shiftDate;
        this.jobPriority = jobPriority;
        this.requiredEmployees = requiredEmployees;
        this.employee = employee;
        this.job = job;
        this.jobId = jobId;
        this.status = status;
        this.version = version;
    }

    // Getter and setter methods
    public Long getShiftPlanId() {
        return id;
    }

    public void setShiftPlanId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Date getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(Date shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Integer getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(Integer jobPriority) {
        this.jobPriority = jobPriority;
    }

    public int getRequiredEmployees() {
        return requiredEmployees;
    }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    // Backward compatibility methods for old test code
    public String getShiftType() {
        // Convert priority to shift type for backward compatibility
        if (jobPriority == null) return "NORMAL";
        switch (jobPriority) {
            case 1: return "CRITICAL";
            case 2: return "HIGH";
            case 3: return "MEDIUM";
            case 4: return "LOW";
            case 5: return "MINIMAL";
            default: return "NORMAL";
        }
    }

    public void setShiftType(String shiftType) {
        // Convert shift type to priority for backward compatibility
        if (shiftType == null) {
            this.jobPriority = 3;
            return;
        }
        switch (shiftType.toUpperCase()) {
            case "CRITICAL": this.jobPriority = 1; break;
            case "HIGH": this.jobPriority = 2; break;
            case "MEDIUM": this.jobPriority = 3; break;
            case "LOW": this.jobPriority = 4; break;
            case "MINIMAL": this.jobPriority = 5; break;
            default: this.jobPriority = 3; break;
        }
    }


}
