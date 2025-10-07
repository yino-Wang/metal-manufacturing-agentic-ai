package com.example.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Location class represented by a unique 5-digit UN Location code.
 */
@Embeddable
public class ScheduleOrder {

    @Column(name = "schedule_order_id", insertable = false, updatable = false)
    private String scheduleOrder;
    public ScheduleOrder(){}
    public ScheduleOrder(String scheduleOrder){this.scheduleOrder = scheduleOrder;}
    public void setScheduleOrder(String unLocCode){this.scheduleOrder = scheduleOrder;}
    public String getScheduleOrder(){return this.scheduleOrder;}
}
