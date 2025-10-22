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

    @TransactionalEventListener
    public void handleJobAddedToMachineEvent(JobAddedToMachineEvent jobAddedToMachineEvent) {
        streamBridge.send("jobAddedToMachineChannel", jobAddedToMachineEvent);
    }
}
