package com.example.application.outboundservices;

import com.example.interfaces.rest.MachineJobScheduledEvent;
import com.example.interfaces.rest.MachineScheduledEvent;
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
    @TransactionalEventListener
    public void handleMachineScheduledEvent(MachineScheduledEvent machineScheduledEvent) {
        streamBridge.send("machineScheduledChannel", machineScheduledEvent);
    }

    @TransactionalEventListener
    public void handleMachineJobScheduledEvent(MachineJobScheduledEvent machineJobScheduledEvent) {
        streamBridge.send("machineJobScheduledChannel", machineJobScheduledEvent);
    }
}
