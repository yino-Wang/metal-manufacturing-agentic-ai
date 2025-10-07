package com.example.domain.model.valueobjects;

import csci318.demo.cargotracker.bookingms.domain.model.entities.ScheduleOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
public class ProductionStep {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Embedded
    @AttributeOverride(name = "scheduleOrder", column = @Column(name = "schedule_order_id"))
    private ScheduleOrder scheduleOrder;
//    @Embedded
//    @AttributeOverride(name = "unLocCode", column = @Column(name = "unload_location_id"))
//    private Location unloadLocation;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    @NotNull
    private Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    @NotNull
    private Date endTime;

    public ProductionStep() {
    }

    public ProductionStep(Long id, ScheduleOrder scheduleOrder, Date startTime, Date endTime) {
        this.id = id;
        this.scheduleOrder = scheduleOrder;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
