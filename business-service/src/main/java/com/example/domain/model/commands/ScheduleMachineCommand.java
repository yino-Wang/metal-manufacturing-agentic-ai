package com.example.domain.model.commands;

// scheduling machine command class
public class ScheduleMachineCommand {

    private String machineName;
    //private String employeeName;

    public ScheduleMachineCommand() {}

    public ScheduleMachineCommand(String machineName) {
        //this.schedulingId = schedulingId;
        this.machineName = machineName;
        //this.employeeName = employeeName;
    }


//    public String getEmployeeName() {
//        return employeeName;
//    }
//    public void setEmployeeName(String employeeName) {
//        this.employeeName = employeeName;
//    }
    public String getMachineName() {
        return machineName;
    }
    public void setMachineName(String machineName) {this.machineName = machineName;}
}
