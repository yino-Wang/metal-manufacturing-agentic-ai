package com.example.application.service;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Objects;

public class MachineMaterialKey {
    private String machineId;
    private String materialName;

    public MachineMaterialKey() {
    }

    public MachineMaterialKey(String machineId, String materialName) {
        this.machineId = machineId;
        this.materialName = materialName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineMaterialKey that = (MachineMaterialKey) o;
        return Objects.equals(machineId, that.machineId) &&
                Objects.equals(materialName, that.materialName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineId, materialName);
    }

    @Override
    public String toString() {
        return "MachineMaterialKey: " +
                machineId + ": " + materialName;
    }
}
