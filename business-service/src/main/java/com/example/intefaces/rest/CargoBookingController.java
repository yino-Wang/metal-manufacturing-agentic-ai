package com.example.intefaces.rest;

import csci318.demo.cargotracker.bookingms.application.commandservices.CargoBookingCommandService;
import csci318.demo.cargotracker.bookingms.application.queryservices.CargoBookingQueryService;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.Job;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.JobScheduleId;
import csci318.demo.cargotracker.bookingms.interfaces.rest.dto.BookCargoResource;
import csci318.demo.cargotracker.bookingms.interfaces.rest.transform.BookCargoCommandDTOAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller    // This means that this class is a Controller
@RequestMapping("/cargobooking")
public class CargoBookingController {


    private CargoBookingCommandService cargoBookingCommandService; // Application Service Dependency

    private CargoBookingQueryService cargoBookingQueryService;

    /**
     * Provide the dependencies
     * @param cargoBookingCommandService
     */
    public CargoBookingController(CargoBookingCommandService cargoBookingCommandService, CargoBookingQueryService cargoBookingQueryService){
        this.cargoBookingCommandService = cargoBookingCommandService;
        this.cargoBookingQueryService = cargoBookingQueryService;
    }

    /**
     * POST method to book a cargo
     * @param bookCargoResource
     */

    @PostMapping
    @ResponseBody
    public JobScheduleId bookCargo(@RequestBody  BookCargoResource bookCargoResource){
        System.out.println("****Cargo Booked ****"+bookCargoResource.getBookingAmount());
        JobScheduleId jobScheduleId = cargoBookingCommandService.bookCargo(
                BookCargoCommandDTOAssembler.toCommandFromDTO(bookCargoResource));

        return jobScheduleId;
    }

    /**
     * GET method to retrieve a Cargo
     * @param bookingId
     * @return Cargo
     */
    @GetMapping("/findCargo")
    @ResponseBody
    public Job findByBookingId(@RequestParam("bookingId") String bookingId){
        System.out.println("****Cargo BookingID ****"+bookingId);
        return cargoBookingQueryService.find(new JobScheduleId(bookingId));
    }

    /**
     * GET method to retrieve a Cargo
     * @param
     * @return List<com.example.domain.model.aggregates.JobScheduleId>
     */
    @GetMapping("/findAllBookingIds")
    @ResponseBody
    public List<JobScheduleId> findAllBookingIds(){
        final List<JobScheduleId> jobScheduleIdList = cargoBookingQueryService.findAllBookingIds();
        System.out.println("****Cargo BookingID ****");
        jobScheduleIdList.forEach(x->System.out.println(x.getBookingId()));
        return jobScheduleIdList;
    }
}
