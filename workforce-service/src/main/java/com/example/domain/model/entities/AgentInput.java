package com.example.domain.model.entities;

import com.example.domain.model.aggregates.Employee;

import java.util.Date;
import java.util.List;
import java.util.Map;

// Model representing the input for the scheduling agent
public class AgentInput {
    private List<Employee> availableEmployees;
    private Date startTime;
    private Date endTime;
    private Map<String, Integer> staffingRequirements;
    private Map<String, Object> constraints;

    // Getters and setters
    public List<Employee> getAvailableEmployees() {
        return availableEmployees;
    }

    public void setAvailableEmployees(List<Employee> availableEmployees) {
        this.availableEmployees = availableEmployees;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Map<String, Integer> getStaffingRequirements() {
        return staffingRequirements;
    }

    public void setStaffingRequirements(Map<String, Integer> staffingRequirements) {
        this.staffingRequirements = staffingRequirements;
    }

    // Alias method for compatibility
    public void setEmployeeRequirements(Map<String, Integer> requirements) {
        this.staffingRequirements = requirements;
    }

    public Map<String, Object> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, Object> constraints) {
        this.constraints = constraints;
    }
}
