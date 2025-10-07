package com.example.intefaces.rest.transform;

import csci318.demo.cargotracker.bookingms.domain.model.commands.ScheduleJobCommand;
import csci318.demo.cargotracker.bookingms.interfaces.rest.dto.BookCargoResource;

/**
 * Assembler class to convert the Book Cargo Resource Data to the Book Cargo Model
 */
public class BookCargoCommandDTOAssembler {

    /**
     * Static method within the Assembler class
     * @param bookCargoResource
     * @return com.example.domain.model.aggregates.ScheduleJobCommand Model
     */
    public static ScheduleJobCommand toCommandFromDTO(BookCargoResource bookCargoResource){

        return new ScheduleJobCommand(
                                    bookCargoResource.getBookingAmount(),
                                    bookCargoResource.getOriginLocation(),
                                    bookCargoResource.getDestLocation(),
                                    java.sql.Date.valueOf(bookCargoResource.getDestArrivalDeadline()));
    }
}
