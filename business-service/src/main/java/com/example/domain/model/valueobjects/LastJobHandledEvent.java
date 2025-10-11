package com.example.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

@Embeddable
public class LastJobHandledEvent {

    private Integer handlingEventId;
    @Transient
    private String handlingEventType;
    // Null object pattern.
    public static final LastJobHandledEvent EMPTY = new LastJobHandledEvent();

    public LastJobHandledEvent() {}

    public LastJobHandledEvent(Integer handlingEventId, String handlingEventType) {
        this.handlingEventId = handlingEventId;
        this.handlingEventType = handlingEventType;
    }

    public Integer getHandlingEventId() {
        return handlingEventId;
    }
    public String getHandlingEventType() {
        return handlingEventType;
    }

    public void setHandlingEventId(Integer handlingEventId) {
        this.handlingEventId = handlingEventId;
    }
    public void setHandlingEventType(String handlingEventType) {
        this.handlingEventType = handlingEventType;
    }

}
