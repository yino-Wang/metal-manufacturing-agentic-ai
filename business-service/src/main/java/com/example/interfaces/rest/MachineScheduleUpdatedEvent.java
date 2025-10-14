package com.example.interfaces.rest;

public class MachineScheduleUpdatedEvent {

    MachineScheduleUpdatedEventData machineScheduleUpdatedEventData;

    public MachineScheduleUpdatedEvent() {
    }

    public MachineScheduleUpdatedEvent(MachineScheduleUpdatedEventData machineScheduleUpdatedEventData) {
        this.machineScheduleUpdatedEventData = machineScheduleUpdatedEventData;
    }

    public MachineScheduleUpdatedEventData getUpdateMachineScheduleEventData() {
        return machineScheduleUpdatedEventData;
    }

    public void setUpdateMachineScheduleEventData(MachineScheduleUpdatedEventData machineScheduleUpdatedEventData) {
        this.machineScheduleUpdatedEventData = machineScheduleUpdatedEventData;
    }

    @Override
    public String toString() {
        return "MachineScheduleUpdatedEvent{" +
                "machineScheduleUpdatedEventData=" + machineScheduleUpdatedEventData +
                '}';
    }
}
