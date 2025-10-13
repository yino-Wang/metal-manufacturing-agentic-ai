package com.example.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public class MachineName {

    private String machineName;

    public MachineName(){}

    public MachineName(String machineName){this.machineName = machineName;}

    public String getMachineName(){return this.machineName;}

    @Override
    public String toString() {
        return "MachineName{" +
                "machineName='" + machineName + '\'' +
                '}';
    }
}
