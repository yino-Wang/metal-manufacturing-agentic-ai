package com.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Machine {

    @Id
    private String machineId;
    @OneToMany(mappedBy = "machine")
    private List<Report> reports;

    public Machine() {
    }

    public Machine(String machineId, List<Report> reports) {
        this.machineId = machineId;
        this.reports = reports;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public List<Report> getMaintenanceReports() {
        return reports;
    }

    public void setMaintenanceReports(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId='" + machineId + '\'' +
                ", maintenanceReports=" + reports +
                '}';
    }

}
