package com.example.domain.model.valueobjects;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Domain class which tracks the progress of the Cargo against the Route Specification / Itinerary and Handling Events.
 */

@Embeddable
public class Delivery {

    public static final Date ETA_UNKOWN = null;

    //Enumerated Types - Routing Status / Transport Status of the Cargo
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status")
    private JobStatus jobStatus; //Routing Status of the Cargo
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_status")
    private PriorityStatus priorityStatus; //Routing Status of the Cargo

    //Current/PRevious information of the Cargo. Helps the operator in determining the current state is OK.
    @Column(name = "last_known_production_step")
    private ProductionStep lastKnownProductionStep;
    @Column(name = "current_required_materials")
    private RequiredMaterials currentRequiredMaterials;
    @Embedded
    private LastJobHandledEvent lastEvent;
    //Predictions for the Cargo activity. Helps the operator in determining if anything needs to be changed for the future
    public static final JobHandlingActivity NO_ACTIVITY = new JobHandlingActivity();
    @Embedded
    private JobHandlingActivity nextExpectedActivity;


    public Delivery() {
        // Nothing to initialize
    }

    public Delivery(LastJobHandledEvent lastEvent, JobItinerary itinerary,
                    JobSpecification jobSpecification) {
        this.lastEvent = lastEvent;
        this.jobStatus = calculateJobStatus(itinerary, jobSpecification);
        this.priorityStatus = calculatePriorityStatus();
        this.lastKnownProductionStep = calculateLastKnownProductionStep();
        this.currentRequiredMaterials = calculateCurrentRequiredMaterials();
        //this.nextExpectedActivity = calculateNextExpectedActivity(
               // jobSpecification, itinerary);
    }

    /**
     * Creates a new delivery snapshot to reflect changes in routing, i.e. when
     * the route specification or the itinerary has changed but no additional
     * handling of the cargo has been performed.
     */
    public Delivery updateOnRouting(JobSpecification jobSpecification,
                                    JobItinerary itinerary) {
        return new Delivery(this.lastEvent, itinerary, jobSpecification);
    }

    /**
     *
     * @param jobSpecification
     * @param itinerary
     * @param lastJobHandledEvent
     * @return
     */

    public static Delivery derivedFrom(JobSpecification jobSpecification,
                                       JobItinerary itinerary, LastJobHandledEvent lastJobHandledEvent) {

        return new Delivery(lastJobHandledEvent, itinerary, jobSpecification);
    }




    /**
     * Method to calculate the Routing status of a Cargo
     *
     * @param itinerary
     * @param jobSpecification
     * @return
     */
    private JobStatus calculateJobStatus(JobItinerary itinerary,
                                             JobSpecification jobSpecification) {
        if (itinerary == null || itinerary == JobItinerary.EMPTY_ITINERARY) {
            return JobStatus.NOTSTARTED;
        }

        switch (lastEvent.getHandlingEventType()) {
            case "INPROGRESS":
                return JobStatus.INPROGRESS;
            case "FINISHED":
                return JobStatus.COMPLETED;
            case "ISSUE":
                return JobStatus.CURRENTISSUE;
            default:
                return JobStatus.UNKNOWN;
        }
    }

    /**
     * Method to calculate the Transposrt Status of a Cargo
     * @return
     */
    private PriorityStatus calculatePriorityStatus() {
        System.out.println("Priority Status for last event"+lastEvent.getHandlingEventType());
        if (lastEvent.getHandlingEventType() == null) {
            return PriorityStatus.NOTGIVEN;
        }

        switch (lastEvent.getHandlingEventType()) {
            case "HIGH":
                return PriorityStatus.HIGH;
            case "MEDIUM":
                return PriorityStatus.MEDIUM;
            case "LOW":
                return PriorityStatus.LOW;
            default:
                return PriorityStatus.NOTGIVEN;
        }
    }

    /**
     * Calculate Last known location
     * @return
     */
    private ProductionStep calculateLastKnownProductionStep() {
        if (lastEvent != null) {
            return new ProductionStep(lastEvent.getHandlingEventProductionStep());
            //new com.example.domain.model.aggregates.ProductionStep(scheduleJobCommand.getFirstProductionStepId(), new com.example.domain.model.aggregates.ScheduleOrder(scheduleJobCommand.getScheduleOrder()), scheduleJobCommand.getStartDate(), scheduleJobCommand.getProjectedEndDate()),
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    private RequiredMaterials calculateCurrentRequiredMaterials() {
        if (getJobStatus().equals(JobStatus.INPROGRESS) && lastEvent != null) {
            return new RequiredMaterials(lastEvent.getHandlingEventRequiredMaterials());
        } else {
            return null;
        }
    }


    public JobStatus getJobStatus() { return this.jobStatus;}
    public PriorityStatus getPriorityStatus() { return this.priorityStatus;}
    public ProductionStep getLastKnownProductionStep() {
        return this.lastKnownProductionStep;
    }
    public void setLastKnownProductionStep(ProductionStep lastKnownProductionStep) {
        this.lastKnownProductionStep = lastKnownProductionStep;
    }
    public void setLastEvent(LastJobHandledEvent lastEvent) {
        this.lastEvent = lastEvent;
    }
    public RequiredMaterials getCurrentRequiredMaterials() {
        return this.currentRequiredMaterials;
    }

}
