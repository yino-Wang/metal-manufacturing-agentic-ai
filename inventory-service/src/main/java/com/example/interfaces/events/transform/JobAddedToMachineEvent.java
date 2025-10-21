package com.example.interfaces.events.transform;

public class JobAddedToMachineEvent {

    private String materialName;
    private int materialRequired;

    public JobAddedToMachineEvent() {}

    public JobAddedToMachineEvent(String materialName, int materialRequired) {
        this.materialName = materialName;
        this.materialRequired = materialRequired;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getMaterialRequired() {
        return materialRequired;
    }

    public void setMaterialRequired(int materialRequired) {
        this.materialRequired = materialRequired;
    }

    @Override
    public String toString() {
        return "JobAddedToMachineEvent{" +
                "materialName='" + materialName + '\'' +
                ", materialRequired=" + materialRequired +
                '}';
    }
}
