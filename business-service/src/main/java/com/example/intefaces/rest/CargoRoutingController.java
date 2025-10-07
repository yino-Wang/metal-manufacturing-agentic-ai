package com.example.intefaces.rest;

import csci318.demo.cargotracker.bookingms.application.commandservices.CargoBookingCommandService;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.JobScheduleId;
import csci318.demo.cargotracker.bookingms.interfaces.rest.dto.RouteCargoResource;
import csci318.demo.cargotracker.bookingms.interfaces.rest.transform.RouteCargoCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping("/cargorouting")
public class CargoRoutingController {

    private CargoBookingCommandService cargoBookingCommandService; // Application Service Dependency


    /**
     * Provide the dependencies
     * @param cargoBookingCommandService
     */
    public CargoRoutingController(CargoBookingCommandService cargoBookingCommandService){
        this.cargoBookingCommandService = cargoBookingCommandService;
    }


    /**
     * POST method to route a cargo
     * @param routeCargoResource
     */
    @PostMapping
    @ResponseBody
    public JobScheduleId routeCargo(@RequestBody RouteCargoResource routeCargoResource){
        cargoBookingCommandService.assignRouteToCargo(
                RouteCargoCommandDTOAssembler
                        .toCommandFromDTO(routeCargoResource));

        JobScheduleId jobScheduleId = new JobScheduleId(routeCargoResource.getBookingId());
        return jobScheduleId;
    }
}
