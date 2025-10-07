package com.example.intefaces.rest.transform;

import csci318.demo.cargotracker.bookingms.domain.model.commands.JobSpecificationJobCommand;
import csci318.demo.cargotracker.bookingms.interfaces.rest.dto.RouteCargoResource;

/**
 * Assembler class to convert the Book Cargo Resource Data to the Book Cargo Model
 */
public class RouteCargoCommandDTOAssembler {

    /**
     * Static method within the Assembler class
     * @param routeCargoResource
     * @return com.example.domain.model.aggregates.JobSpecificationJobCommand Model
     */
    public static JobSpecificationJobCommand toCommandFromDTO(RouteCargoResource routeCargoResource){

        return new JobSpecificationJobCommand(routeCargoResource.getBookingId());
    }
}
