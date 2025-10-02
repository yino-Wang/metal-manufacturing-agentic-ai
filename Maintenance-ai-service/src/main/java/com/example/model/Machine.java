package com.example.model;

import jakarta.persistence.*;
import java.util.List;

public class Machine {

    @Id
    private String machineId;
    @OneToMany(mappedBy = "machine")
    private List<MaintenanceReport> maintenanceReports;

    public Machine() {
    }

    public Machine(String machineId, List<MaintenanceReport> maintenanceReports) {
        this.machineId = machineId;
        this.maintenanceReports = maintenanceReports;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public List<MaintenanceReport> getMaintenanceReports() {
        return maintenanceReports;
    }

    public void setMaintenanceReports(List<MaintenanceReport> maintenanceReports) {
        this.maintenanceReports = maintenanceReports;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId='" + machineId + '\'' +
                ", maintenanceReports=" + maintenanceReports +
                '}';
    }

}
