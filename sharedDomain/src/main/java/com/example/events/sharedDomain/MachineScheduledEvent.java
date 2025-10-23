package com.example.events.sharedDomain;

public class MachineScheduledEvent {

    MachineScheduledEventData machineScheduledEventData;

    public MachineScheduledEvent(){}

    public MachineScheduledEvent(MachineScheduledEventData machineScheduledEventData){
        this.machineScheduledEventData = machineScheduledEventData;
    }

    public MachineScheduledEventData getMachineScheduledEventData() {
        return machineScheduledEventData;
    }

    public void setMachineScheduledEventData(MachineScheduledEventData machineScheduledEventData) {
        this.machineScheduledEventData = machineScheduledEventData;
    }

    @Override
    public String toString() {
        return "MachineScheduledEvent{" +
                "machineJobScheduledEventData=" + machineScheduledEventData +
                '}';
    }
}
