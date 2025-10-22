//package com.example.application.outboundservices;
//
//import com.example.events.JobAddedToMachineEvent;
//import org.springframework.cloud.stream.function.StreamBridge;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.event.TransactionalEventListener;
//
///**
// * Service to publish inventory-related events such as job material allocation.
// */
//@Service
//public class InventoryEventPublisherService {
//
//    private final StreamBridge streamBridge;
//
//    public InventoryEventPublisherService(StreamBridge streamBridge) {
//        this.streamBridge = streamBridge;
//    }
//
//    @TransactionalEventListener
//    public void handleJobAddedToMachineEvent(JobAddedToMachineEvent jobAddedToMachineEvent) {
//        // Send event to Kafka (or another messaging system)
//        streamBridge.send("inventoryChannel", jobAddedToMachineEvent);
//    }
//}
