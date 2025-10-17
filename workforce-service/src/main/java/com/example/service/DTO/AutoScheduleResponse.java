package com.example.service.DTO;

import com.example.domain.model.entities.ShiftPlan;
import com.example.domain.model.aggregates.Employee;
import java.util.List;

// Response DTO for auto-scheduling shifts
public class AutoScheduleResponse {
    private List<ShiftPlan> shiftPlans;
    private List<Employee> alternatives;

    public List<ShiftPlan> getShiftPlans() {
        return shiftPlans;
    }

    public void setShiftPlans(List<ShiftPlan> shiftPlans) {
        this.shiftPlans = shiftPlans;
    }

    // Backward compatibility methods
    public List<ShiftPlan> getShiftSchedules() {
        return shiftPlans;
    }

    public void setShiftSchedule(List<ShiftPlan> shiftPlans) {
        this.shiftPlans = shiftPlans;
    }

    public List<Employee> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Employee> alternatives) {
        this.alternatives = alternatives;
    }
}