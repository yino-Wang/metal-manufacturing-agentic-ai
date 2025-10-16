package com.example.application.service;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.valueobjects.Job;
import com.example.infrastructure.repositories.MachineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulingService {

    private final MachineRepository machineRepository;

    public SchedulingService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional
    public void processAllMachines() {
        // Fetch all machines from the database
        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            System.out.println("Processing jobs for machine: " + machine.getMachineId());
            processJobsForMachine(machine);
        }
    }

    private void processJobsForMachine(Machine machine) {
        // Jobs are already ordered by submitDate due to the @OrderBy annotation on the Machine entity
        List<Job> jobs = machine.getJobList().getJobs();

        if (jobs.isEmpty()) {
            System.out.println("  - No jobs found for machine.");
            return;
        }

        LocalDate lastJobEndTime = null;

        for (Job job : jobs) {
            LocalDate newStartTime;

            if (lastJobEndTime == null || lastJobEndTime.isBefore(job.getSubmitDate())) {
                newStartTime = job.getSubmitDate();
            } else {
                newStartTime = lastJobEndTime;
            }

            job.setStartDate(newStartTime);
            job.setEndDate(newStartTime.plusDays(job.getJobTimeNeededDays()));
            lastJobEndTime = job.getEndDate();

            // The changes are automatically tracked by the persistence context
            // and will be saved when the @Transactional method completes.
            System.out.println("  - Updated job " + job.getJobNumber() + ": start=" + job.getStartDate() + ", end=" + job.getEndDate());
        }
    }
}