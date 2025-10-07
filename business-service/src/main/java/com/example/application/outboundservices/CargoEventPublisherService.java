package com.example.application.outboundservices;

import csci318.demo.cargotracker.shareddomain.events.CargoRoutedEvent;
import csci318.demo.cargotracker.shareddomain.events.JobScheduledEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 */
@Service
public class CargoEventPublisherService {

    private final StreamBridge streamBridge;

    public CargoEventPublisherService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @TransactionalEventListener
    public void handleCargoBookedEvent(JobScheduledEvent jobScheduledEvent) {
        streamBridge.send("cargoBookingChannel", jobScheduledEvent);
    }

    @TransactionalEventListener
    public void handleCargoRoutedEvent(CargoRoutedEvent cargoRoutedEvent) {
        streamBridge.send("cargoRoutingChannel", cargoRoutedEvent);
    }
}
