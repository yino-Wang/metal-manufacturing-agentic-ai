package com.example.domain.event;

import com.example.domain.model.ShiftSchedule;
import org.springframework.context.ApplicationEvent;

/**
 * when a shift plan is published, this event is triggered
 * Represents the event of publishing a shift plan.
 */
public class ShiftPublished extends ApplicationEvent {
    private final ShiftSchedule shiftSchedule;

    public ShiftPublished(ShiftSchedule shiftSchedule) {
        super(shiftSchedule);
        this.shiftSchedule = shiftSchedule;
    }

    public ShiftSchedule getShiftSchedule() {
        return shiftSchedule;
    }
}
