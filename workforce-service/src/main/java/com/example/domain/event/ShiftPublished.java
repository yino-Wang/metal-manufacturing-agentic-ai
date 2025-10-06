package com.example.domain.event;

import com.example.domain.model.entities.ShiftSchedule;

/**
 * Cross-service event message body: Shift schedule published event
 * Used as Kafka message body for microservice communication only
 */
public class ShiftPublished {
    private final ShiftSchedule shiftSchedule;

    public ShiftPublished(ShiftSchedule shiftSchedule) {
        this.shiftSchedule = shiftSchedule;
    }

    public ShiftSchedule getShiftSchedule() {
        return shiftSchedule;
    }
}
