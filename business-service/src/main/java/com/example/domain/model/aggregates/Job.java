package com.example.domain.model.aggregates;

import com.example.domain.model.valueobjects.*;
import com.example.domain.model.commands.ScheduleJobCommand;
import com.example.domain.model.entities.ScheduleOrder;
import com.example.domain.model.valueobjects.*;
import com.example.shareddomain.events.CargoRoutedEvent;
import com.example.shareddomain.events.CargoRoutedEventData;
import com.example.shareddomain.events.JobScheduledEvent;
import com.example.shareddomain.events.JobScheduledEventData;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@NamedQueries({
        @NamedQuery(name = "com.example.domain.model.aggregates.Job.findAll",
                query = "Select j from com.example.domain.model.aggregates.Job j"),
        @NamedQuery(name = "com.example.domain.model.aggregates.Job.findByJobScheduleId",
                query = "Select j from com.example.domain.model.aggregates.Job j where j.jobScheduleId = ?1"),
        @NamedQuery(name = "com.example.domain.model.aggregates.Job.findAllScheduleIds",
                query = "Select j.jobScheduleId from com.example.domain.model.aggregates.Job j") })
public class Job extends AbstractAggregateRoot<Job> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private JobScheduleId jobScheduleId; // Aggregate Identifier
    @Embedded
    private QueuePosition queuePosition; //Booking Amount
    @Embedded
    private ScheduleOrder scheduleOrder; //Origin Location of the Cargo
    @Embedded
    private JobSpecification jobSpecification; //Route Specification of the Cargo
    @Embedded
    private JobItinerary itinerary; //Itinerary Assigned to the Cargo
    @Embedded
    private Delivery delivery; // Checks the delivery progress of the cargo against the actual Route Specification and Itinerary
    @Embedded
    private RequiredMaterials requiredMaterials;

    /**
     * Default Constructor
     */
    public Job() {
        // Nothing to initialize.
    }

    /**
     * Constructor Command Handler for a new Cargo booking. Sets the state of the Aggregate
     * and registers the Cargo Booked Event
     *
     */
    public Job(ScheduleJobCommand scheduleJobCommand){
        this.jobScheduleId = new JobScheduleId(scheduleJobCommand.getJobScheduleId());
        this.jobSpecification = new JobSpecification(
                new ProductionStep(scheduleJobCommand.getFirstProductionStepId(), new ScheduleOrder(scheduleJobCommand.getScheduleOrder()), scheduleJobCommand.getStartDate(), scheduleJobCommand.getProjectedEndDate()),
                new ProductionStep(scheduleJobCommand.getNextProductionStepId(), new ScheduleOrder(scheduleJobCommand.getScheduleOrder()), scheduleJobCommand.getStartDate(), scheduleJobCommand.getProjectedEndDate()),
                scheduleJobCommand.getSubmitDate(),
                scheduleJobCommand.getStartDate(),
                scheduleJobCommand.getProjectedEndDate()
        );
        this.itinerary = JobItinerary.EMPTY_ITINERARY; //Empty Itinerary since the Cargo has not been routed yet
        this.queuePosition = new QueuePosition(scheduleJobCommand.getQueuePosition());
        this.delivery = Delivery.derivedFrom(this.jobSpecification,
                this.itinerary, LastJobHandledEvent.EMPTY);

        //Add this domain event which needs to be fired when the new cargo is saved
        addDomainEvent(new
                JobScheduledEvent(
                new JobScheduledEventData(jobScheduleId.getJobScheduleId(),
                        scheduleJobCommand.getQueuePosition(),
                        scheduleJobCommand.getScheduleOrder(),
                        scheduleJobCommand.getFirstProductionStep(),
                        scheduleJobCommand.getNextProductionStep())));
    }

    public JobScheduleId getBookingId() {
        return jobScheduleId;
    }

    public void setScheduleOrder(ScheduleOrder scheduleOrder) {
        this.scheduleOrder = scheduleOrder;
    }

    public ScheduleOrder getScheduleOrder() {
        return scheduleOrder;
    }

    public JobSpecification getJobSpecification() {
        return this.jobSpecification;
    }


    public QueuePosition getBookingAmount(){
        return this.queuePosition;
    }

    public void setBookingAmount(QueuePosition queuePosition){
        this.queuePosition = queuePosition;
    }
    /**
     * @return The itinerary
     */
    public JobItinerary getItinerary() {
        return this.itinerary;
    }

    /**
     * Command Handler for the Route Cargo Command. Sets the state of the Aggregate and registers the
     * Cargo routed event
     * @param jobItinerary
     */

    public void assignToRoute(JobItinerary jobItinerary) {
        this.itinerary = jobItinerary;
        //Add this domain event which needs to be fired when the new cargo is saved
        addDomainEvent(new
                CargoRoutedEvent(new CargoRoutedEventData(this.jobScheduleId.getJobScheduleId())));
    }

    /**
     *
     * @param lastJobHandledEvent
     */
    public void deriveDeliveryProgress(LastJobHandledEvent lastJobHandledEvent) {
        this.delivery = Delivery.derivedFrom(getJobSpecification(), getItinerary(),
                lastJobHandledEvent);
    }

    /**
     * Method to register the event
     * @param event
     */
    public void addDomainEvent(Object event){
        registerEvent(event);
    }


}