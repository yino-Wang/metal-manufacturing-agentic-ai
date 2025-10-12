package com.example.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CurrentJob {
    @Enumerated(EnumType.STRING)
    @Column(name = "currentJobStatus")
    private JobStatus currentJobStatus;
    @Embedded
    private LastJobHandledEvent lastEvent;
    //Predictions for the Cargo activity. Helps the operator in determining if anything needs to be changed for the future
    public static final LastJobHandledEvent NO_ACTIVITY = new LastJobHandledEvent();
    @Embedded
    private JobHandlingActivity nextExpectedActivity;

    /**
     * Method to calculate the schedule status of the machine
     *
     * @param jobList
     * @return
     */
    private ScheduleStatus calculateScheduleStatus(JobList jobList) {
        if (jobList == null || jobList == JobList.EMPTY_LIST) {
            return ScheduleStatus.EMPTY;
        } else {
            return ScheduleStatus.IN_PROGRESS;
        }
    }

//    /**
//     * Method to calculate the job status of the current job of a machine
//     */
//    private JobStatus calculateJobStatus() {
//        if (lastEvent == null || lastEvent == LastJobHandledEvent.NO_EVENT) {
//            return JobStatus.NOT_STARTED;
//        }
//
//        return switch (lastEvent.getHandlingEventType()) {
//            case "PENDING" -> JobStatus.PENDING;
//            case "IN_PROGRESS" -> JobStatus.IN_PROGRESS;
//            case "COMPLETED" -> JobStatus.COMPLETED;
//            default -> JobStatus.UNKNOWN;
//        };
//    }
}
