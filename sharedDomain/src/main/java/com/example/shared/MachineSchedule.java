package com.example.shared;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MachineSchedule {
    private Map<String, List<JobDto>> schedules;

    public MachineSchedule() {}

    public MachineSchedule(Map<String, List<JobDto>> schedules) {
        this.schedules = schedules;
    }

    public Map<String, List<JobDto>> getSchedules() {
        return schedules;
    }

    public void setSchedules(Map<String, List<JobDto>> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "MachineSchedule{" +
                "schedules=" + schedules +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineSchedule)) return false;
        MachineSchedule that = (MachineSchedule) o;
        return Objects.equals(schedules, that.schedules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedules);
    }
}
