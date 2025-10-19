package com.example.domain.model.commands;
import java.util.Date;

/**
 * Command for generating shift plan.
 * Encapsulates all data required for the GenerateShiftPlan use case.
 */
public class GenerateShiftPlanCommand {
    private Long jobId;
    private Integer jobPriority;  // Add job priority
    private Long employeeId;
    private Date startDate;
    private Date endDate;
    private int requiredEmployees;
    // Remove shiftType field

    public GenerateShiftPlanCommand(Long jobId, Integer jobPriority, Long employeeId, Date startDate, Date endDate, int requiredEmployees) {
        this.jobId = jobId;
        this.jobPriority = jobPriority;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requiredEmployees = requiredEmployees;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(Integer jobPriority) {
        this.jobPriority = jobPriority;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getRequiredEmployees() {
        return requiredEmployees;
    }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees;
    }
}
