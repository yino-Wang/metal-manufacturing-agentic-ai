package com.example.application.queryservices;

import csci318.demo.cargotracker.bookingms.domain.model.aggregates.Job;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.JobScheduleId;
import csci318.demo.cargotracker.bookingms.infrastructure.repositories.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service which caters to all queries related to the Booking Bounded Context
 */
@Service
public class CargoBookingQueryService {


    private JobRepository jobRepository; // Inject Dependencies

    public CargoBookingQueryService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    /**
     * Find all Cargos
     * @return List<Cargo>
     */

    public List<Job> findAll(){
        return jobRepository.findAll();
    }

    /**
     * List All Booking Identifiers
     * @return List<com.example.domain.model.aggregates.JobScheduleId>
     */
   public List<JobScheduleId> findAllBookingIds(){

       return jobRepository.findAllBookingIds();
   }

    /**
     * Find a specific Cargo based on its Booking Id
     * @param jobScheduleId
     * @return Cargo
     */
    public Job find(JobScheduleId jobScheduleId){
        return jobRepository.findByBookingId(jobScheduleId);
    }
}
