package com.example.application.commandservices;

import csci318.demo.cargotracker.bookingms.application.outboundservices.acl.ExternalJobSchedulingService;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.Job;
import csci318.demo.cargotracker.bookingms.domain.model.aggregates.JobScheduleId;
import csci318.demo.cargotracker.bookingms.domain.model.commands.JobSpecificationJobCommand;
import csci318.demo.cargotracker.bookingms.domain.model.commands.ScheduleJobCommand;
import csci318.demo.cargotracker.bookingms.domain.model.valueobjects.JobItinerary;
import csci318.demo.cargotracker.bookingms.infrastructure.repositories.JobRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application Service class for the Cargo Booking Commands
 */

@Service
public class ScheduleJobCommandService {


    private final JobRepository jobRepository;
    private final ExternalJobSchedulingService externalJobSchedulingService;

    public ScheduleJobCommandService(JobRepository jobRepository, ExternalJobSchedulingService externalJobSchedulingService) {

        this.jobRepository = jobRepository;
        this.externalJobSchedulingService = externalJobSchedulingService;
    }

    /**
     * Service Command method to book a new Cargo
     *
     * @return com.example.domain.model.aggregates.JobScheduleId of the Cargo
     */

    public JobScheduleId scheduleJob(ScheduleJobCommand scheduleJobCommand) {

        String random = UUID.randomUUID().toString().toUpperCase();
        String jobIdStr = random.substring(0, random.indexOf("-"));
        System.out.println("Random is :" + jobIdStr);
        scheduleJobCommand.setJobScheduleId(jobIdStr);
        Job job = new Job(scheduleJobCommand);
        jobRepository.save(job);
        return new JobScheduleId(jobIdStr);
    }

    /**
     * Service Command method to assign a route to a Cargo
     * @param jobSpecificationJobCommand
     */

    public void assignRouteToCargo(JobSpecificationJobCommand jobSpecificationJobCommand){
        System.out.println("com.example.domain.model.aggregates.Job - JobSchedule command"+ jobSpecificationJobCommand.getJobJobScheduleId());
        Job job = jobRepository.findByJobId(
                new JobScheduleId(jobSpecificationJobCommand.getJobJobScheduleId()));
        JobItinerary jobItinerary = externalJobSchedulingService
                .fetchRouteForSpecification(job.getJobSpecification());

        job.assignToRoute(jobItinerary);
        jobRepository.save(job);
    }
}
