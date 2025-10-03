package com.example.domain.event;
import com.example.domain.model.Timesheet;

import jakarta.persistence.*;
import org.springframework.context.ApplicationEvent;

public class TimesheetEvent extends ApplicationEvent {
    private final Timesheet timesheet;

    public TimesheetEvent(Timesheet timesheet) {
        super(timesheet);
        this.timesheet = timesheet;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }
}

