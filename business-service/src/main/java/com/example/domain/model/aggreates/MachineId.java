package com.example.domain.model.aggreates;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class MachineId implements Serializable {

    @Column(name="machine_id")
    private String machineId;

    public MachineId(){}

    public MachineId(String machineId){this.machineId = machineId;}

    public String getMachineId(){return this.machineId;}

    @Override
    public String toString() {
        return "MachineId{" +
                "schedulingId='" + machineId + '\'' +
                '}';
    }
}
