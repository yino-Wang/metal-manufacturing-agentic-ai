package com.example.domain.event;
import com.example.domain.model.Timesheet;
import com.example.service.usecase.RecordTimesheetService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class TimesheetEvent {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Long timesheetId;
    @Column
    private String eventName; // e.g., "CREATED", "UPDATED", "DELETED"
    @Column
    private String timestamp;

    // Constructors, getters, and setters
    public TimesheetEvent() {}
    public TimesheetEvent(Long timesheetId, String eventName, String timestamp) {
        this.timesheetId = timesheetId;
        this.eventName = eventName;
        this.timestamp = timestamp;
    }

    public TimesheetEvent(RecordTimesheetService recordTimesheetService, Timesheet timesheet) {
        this.timesheetId = timesheet.getId();
        this.eventName = "";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventType) {
        this.eventName = eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
