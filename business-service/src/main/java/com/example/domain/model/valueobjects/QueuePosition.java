package com.example.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Domain model representation of the Booking Amount for a new Cargo.
 * Contains the Booking Amount of the Cargo
 */
@Embeddable
public class QueuePosition {

    @Column(name = "queue_position", unique = false, updatable= false)
    private Integer queuePosition;

    public QueuePosition(){}

    public QueuePosition(Integer queuePosition){this.queuePosition = queuePosition;}

    public void setQueuePosition(Integer queuePosition){this.queuePosition = queuePosition;}

    public Integer getQueuePosition(){return this.queuePosition;}
}
