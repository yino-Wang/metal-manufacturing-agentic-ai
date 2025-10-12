package com.example.interfaces.rest;

public class JobAddedToMachineEvent {

    JobAddedToMachineEventData jobAddedToMachineEventData;

    public JobAddedToMachineEvent() {}

    public JobAddedToMachineEvent(JobAddedToMachineEventData jobAddedToMachineEventData) {
        this.jobAddedToMachineEventData = jobAddedToMachineEventData;
    }

    public JobAddedToMachineEventData getJobAddedToMachineEventData() {
        return jobAddedToMachineEventData;
    }

    public void setJobAddedToMachineEventData(JobAddedToMachineEventData jobAddedToMachineEventData) {
        this.jobAddedToMachineEventData = jobAddedToMachineEventData;
    }

    @Override
    public String toString() {
        return "JobAddedToMachineEvent{" +
                "jobAddedToMachineEventData=" + jobAddedToMachineEventData +
                '}';
    }
}