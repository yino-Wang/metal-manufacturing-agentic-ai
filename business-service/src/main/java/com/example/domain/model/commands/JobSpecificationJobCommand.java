package com.example.domain.model.commands;

/**
 * Command Class to assign a route to a booked cargo
 */
public class JobSpecificationJobCommand {
    private String jobJobScheduleId;

    public JobSpecificationJobCommand(){ }

    public JobSpecificationJobCommand(String jobJobScheduleId){
        this.setJobJobScheduleId(jobJobScheduleId);
    }


    public String getJobJobScheduleId() {
        return jobJobScheduleId;
    }

    public void setJobJobScheduleId(String jobJobScheduleId) {
        this.jobJobScheduleId = jobJobScheduleId;
    }


}
