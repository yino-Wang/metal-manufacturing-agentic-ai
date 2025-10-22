package com.example.application.queryservices;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.entities.Job;
import com.example.infrastructure.repositories.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Job findJobByJobNumber(Integer jobNumber) {
        return machineRepository.findJobByJobNumber(jobNumber);
    }

    public Optional<Job> findJobInfoByJobNumber(Integer jobNumber) {
        return machineRepository.findJobInfoByJobNumber(jobNumber);
    }

    public List<Job> findAllCustomerJobsByCustomerName(String customerName) {
        return machineRepository.findAllCustomerJobsByCustomerName((customerName));
    }

    public Machine findScheduleByMachineId(String machineId) {
        return machineRepository.findByMachineId(new MachineId(machineId));
    }

    public List<Machine> findAllMachines() {
        return machineRepository.findAll();
    }

}
