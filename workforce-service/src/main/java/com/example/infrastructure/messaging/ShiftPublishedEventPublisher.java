package com.example.infrastructure.messaging;

import com.example.domain.event.ShiftPublished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * ShiftPublishedEventPublisher
 * Publishes shift schedule events to other microservices via Kafka channel for cross-service asynchronous communication.
 * Channel name: shiftPublished-out-0
 */
@Component
public class ShiftPublishedEventPublisher {
    private final StreamBridge streamBridge;

    @Autowired
    public ShiftPublishedEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Publish shift schedule event to Kafka channel
     * @param event ShiftPublished message body
     */
    public void publish(ShiftPublished event) {

        streamBridge.send("shiftPublished-out-0", event);
    }
}
