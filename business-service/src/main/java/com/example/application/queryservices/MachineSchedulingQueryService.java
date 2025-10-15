package com.example.application.queryservices;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Job;
import com.example.infrastructure.repositories.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service which caters to all queries related to the business service Context
 */
@Service
public class MachineSchedulingQueryService {
    private MachineRepository machineRepository; // Inject Dependencies

    public MachineSchedulingQueryService(MachineRepository machineRepository){
        this.machineRepository = machineRepository;
    }

    /**
     * Find all machines
     * @return List<Machine>
     */

    public List<Machine> findAll(){
        return machineRepository.findAll();
    }

    /**
     * List All Machine Identifiers
     * @return List<MachineId>
     */
    public List<MachineId> findAllMachineId(){

        return machineRepository.findAllMachineId();
    }

    /**
     * Find a specific Machine based on its Scheduling Id
     * @param machineId
     * @return Machine
     */
    public Machine find(MachineId machineId){
        return machineRepository.findByMachineId(machineId);
    }

    /**
     * Find currentJob by schedule Id
     * @param machineId
     * @return Job
     */
    public Job findCurrentJobByMachineId(MachineId machineId) {
        return machineRepository.findCurrentJobByMachineId(machineId);
    }

    /**
     * Find all jobs scheduled for a machine by its scheduling Id
     * @param machineId
     * @return Machine
     */
    public Machine findAllJobsByMachineId(MachineId machineId) {
        return machineRepository.findAllJobsByMachineId(machineId);
    }

    /**
     * Returns a valid job schedule, making sure times are in order and JobStatus is correctly assigned for each job
     */
//    @Transactional
//    public void updateJobSchedule() {
//        List<Machine> machines = machineRepository.findAll();
//        for (Machine machine : machines) {
//            List<Job> jobs = machine.getJobs();
//            jobs.sort((job1, job2) -> job1.getStartTime().compareTo(job2.getStartTime()));
//            for (int i = 0; i < jobs.size(); i++) {
//                Job job = jobs.get(i);
//                if (i == 0) {
//                    job.setJobStatus(Job.JobStatus.IN_PROGRESS);
//                } else if (i == jobs.size() - 1) {
//                    job.setJobStatus(Job.JobStatus.SCHEDULED);
//                } else {
//                    job.setJobStatus(Job.JobStatus.SCHEDULED);
//                }
//                if (i > 0) {
//                    Job previousJob = jobs.get(i - 1);
//                    if (job.getStartTime().isBefore(previousJob.getEndTime())) {
//                        throw new IllegalStateException("Job times are overlapping for machine: " + machine.getSchedulingId().getSchedulingId());
//                    }
//                }
//            }
//            machine.setJobs(jobs);
//            machineRepository.save(machine);
//        }
//    }

}
