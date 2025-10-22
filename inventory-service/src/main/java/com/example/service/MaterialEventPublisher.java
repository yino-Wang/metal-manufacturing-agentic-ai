package com.example.service;

import com.example.domain.event.MaterialAllocatedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class MaterialEventPublisher {

    private final StreamBridge streamBridge;

    public MaterialEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishMaterialAllocatedEvent(String materialName, long quantityAllocated) {
        MaterialAllocatedEvent event = new MaterialAllocatedEvent(materialName, quantityAllocated);
        streamBridge.send("materialAllocatedChannel", event);
    }
}
