//package com.example.application.outboundservices;
//
//import com.example.events.JobAddedToMachineEvent;
//import org.springframework.cloud.stream.function.StreamBridge;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.event.TransactionalEventListener;
//
///**
// * Publishes JobAddedToMachineEvent to Kafka via StreamBridge.
// */
//@Service
//public class JobAddedToMachineEventPublisher {
//
//    private final StreamBridge streamBridge;
//
//    public JobAddedToMachineEventPublisher(StreamBridge streamBridge) {
//        this.streamBridge = streamBridge;
//    }
//
//    /**
//     * Listens for JobAddedToMachineEvent and publishes it to Kafka.
//     */
//    @TransactionalEventListener
//    public void handleJobAddedToMachineEvent(JobAddedToMachineEvent jobAddedToMachineEvent) {
//        streamBridge.send("jobAddedToMachineChannel", jobAddedToMachineEvent);
//    }
//}
