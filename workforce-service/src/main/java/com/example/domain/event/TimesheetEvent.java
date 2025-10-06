package com.example.domain.event;
import com.example.domain.model.entities.Timesheet;

/**
 * Cross-service event message body: Timesheet event
 * Used as Kafka message body for microservice communication only
 */
public class TimesheetEvent {
    private final Timesheet timesheet;

    public TimesheetEvent(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }
}
