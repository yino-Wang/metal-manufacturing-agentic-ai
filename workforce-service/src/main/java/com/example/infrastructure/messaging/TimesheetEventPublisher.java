package com.example.infrastructure.messaging;

import com.example.domain.event.TimesheetEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * TimesheetEventPublisher
 * Publishes timesheet events to other microservices via Kafka channel for cross-service asynchronous communication.
 * Channel name: timesheetEvent-out-0
 */
@Component
public class TimesheetEventPublisher {
    private final StreamBridge streamBridge;

    @Autowired
    public TimesheetEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Publish timesheet event to Kafka channel
     * @param event TimesheetEvent message body
     */
    public void publish(TimesheetEvent event) {
        streamBridge.send("timesheetEvent-out-0", event);
    }
}
