package com.example.interfaces.rest.dto;


// attributes required to schedule a machine from a user
public class ScheduleMachineResource {
    private String machineName;
    private String employeeName;

    public ScheduleMachineResource() {}

    public ScheduleMachineResource(String machineName, String employeeName) {
        //this.schedulingId = schedulingId;
        this.machineName = machineName;
        this.employeeName = employeeName;
    }

//    public String getSchedulingId() {
//        return schedulingId;
//    }
//    public void setSchedulingId(String schedulingId) {
//        this.schedulingId = schedulingId;
//    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getMachineName() {
        return machineName;
    }
    public void setMacineName(String machineName) {
        this.machineName = machineName;
    }
}
