package com.example.domain.commands;

public class AddJobMaterialsCommand {

    private int jobNumber;
    private String materialName;
    private int materialAmount;

    public AddJobMaterialsCommand() {
    }

    public AddJobMaterialsCommand(int jobNumber, String materialName, int materialAmount) {
        this.jobNumber = jobNumber;
        this.materialName = materialName;
        this.materialAmount = materialAmount;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(int materialAmount) {
        this.materialAmount = materialAmount;
    }
}
