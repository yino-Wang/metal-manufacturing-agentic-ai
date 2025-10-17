package com.example.domain.event;

import com.example.domain.model.entities.ShiftPlan;

/**
 * Cross-service event message body: Shift schedule published event
 * Used as Kafka message body for microservice communication only
 */
public class ShiftPublished {
    private final ShiftPlan shiftPlan;

    public ShiftPublished(ShiftPlan shiftPlan) {
        this.shiftPlan = shiftPlan;
    }

    public ShiftPlan getShiftSchedule() {
        return shiftPlan;
    }
}
