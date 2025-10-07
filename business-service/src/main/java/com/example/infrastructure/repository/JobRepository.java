package com.example.infrastructure.repository;

import csci318.demo.cargotracker.bookingms.domain.model.aggregates.Job;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.JobScheduleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository class for the Cargo Aggregate
 */
public interface JobRepository extends JpaRepository<Job, Long> {

     Job findByJobId(JobScheduleId JobScheduleId);

     List<JobScheduleId> findAllJobIds();

     List<Job> findAll();

}
