package com.example.domain.model.aggreates;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class SchedulingId implements Serializable {

    @Column(name="scheduling_id")
    private String schedulingId;

    public SchedulingId(){}

    public SchedulingId(String schedulingId){this.schedulingId = schedulingId;}

    public String getSchedulingId(){return this.schedulingId;}

}
