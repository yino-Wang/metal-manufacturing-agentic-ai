package com.example.events;

import com.example.shared.MaterialDTO;

public class JobAddedToMachineEvent {

    private JobAddedToMachineEventData jobAddedToMachineEventData;
    private MaterialDTO materialDTO;

    public JobAddedToMachineEvent(JobAddedToMachineEventData jobAddedToMachineEventData, MaterialDTO materialDTO) {
        this.jobAddedToMachineEventData = jobAddedToMachineEventData;
        this.materialDTO = materialDTO;
    }

    public JobAddedToMachineEventData getJobAddedToMachineEventData() {
        return jobAddedToMachineEventData;
    }

    public void setJobAddedToMachineEventData(JobAddedToMachineEventData jobAddedToMachineEventData) {
        this.jobAddedToMachineEventData = jobAddedToMachineEventData;
    }

    public MaterialDTO getMaterialDTO() {
        return materialDTO;
    }

    public void setMaterialDTO(MaterialDTO materialDTO) {
        this.materialDTO = materialDTO;
    }

    @Override
    public String toString() {
        return "JobAddedToMachineEvent{" +
                "jobAddedToMachineEventData=" + jobAddedToMachineEventData +
                ", materialDTO=" + materialDTO +
                '}';
    }
}
