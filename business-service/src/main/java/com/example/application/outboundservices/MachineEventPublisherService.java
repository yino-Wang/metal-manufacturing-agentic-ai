package com.example.application.outboundservices;

import com.example.events.JobAddedToMachineEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class MachineEventPublisherService {
    private final StreamBridge streamBridge;

    public MachineEventPublisherService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    //maybe don't need if this only creates a machine without a job
//    @TransactionalEventListener
//    public void handleMachineScheduledEvent(MachineScheduledEvent machineScheduledEvent) {
//        streamBridge.send("machineScheduledChannel", machineScheduledEvent);
//    }

    @TransactionalEventListener
    public void handleJobAddedToMachineEvent(JobAddedToMachineEvent jobAddedToMachineEvent) {
        streamBridge.send("jobAddedToMachineChannel", jobAddedToMachineEvent);
    }

//    @TransactionalEventListener
//    public void handleMachineScheduleUpdatedEvent(MachineScheduleUpdatedEvent machineScheduleUpdatedEvent) {
//        streamBridge.send("machineScheduleUpdatedChannel", machineScheduleUpdatedEvent);
//    }
}
