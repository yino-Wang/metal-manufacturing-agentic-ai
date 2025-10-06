package com.example.domain.event;

import com.example.domain.model.entities.Timesheet;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Cross-service event message body: Timesheet event
 * Used as Kafka message body for microservice communication 
 * and persisted for event sourcing/audit trail
 */
@Entity
@Table(name = "timesheet_event")
public class TimesheetEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "timesheet_id", referencedColumnName = "timesheetId")
    private Timesheet timesheet;

    @Column(name = "event_type")
    private String eventType; // CREATE, UPDATE, APPROVE, REJECT, etc.

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "event_data", length = 1000)
    private String eventData; // JSON string for additional event details

    // Constructors
    public TimesheetEvent() {
        this.eventTimestamp = LocalDateTime.now();
    }

    public TimesheetEvent(Timesheet timesheet) {
        this();
        this.timesheet = timesheet;
    }

    public TimesheetEvent(Timesheet timesheet, String eventType) {
        this(timesheet);
        this.eventType = eventType;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
}
