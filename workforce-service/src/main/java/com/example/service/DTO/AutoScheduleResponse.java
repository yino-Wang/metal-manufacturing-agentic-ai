package com.example.service.DTO;

import com.example.domain.model.ShiftSchedule;
import com.example.domain.model.Employee;
import java.util.List;

// Response DTO for auto-scheduling shifts

public class AutoScheduleResponse {
    private List<ShiftSchedule> shiftSchedules;
    private List<Employee> alternatives;

    public List<ShiftSchedule> getShiftSchedules() {
        return shiftSchedules;
    }

    public void setShiftSchedule(List<ShiftSchedule> shiftSchedules) {
        this.shiftSchedules = shiftSchedules; }

    public List<Employee> getAlternatives() {
        return alternatives; }

    public void setAlternatives(List<Employee> alternatives) {
        this.alternatives = alternatives; }

}