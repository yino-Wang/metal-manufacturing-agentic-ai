package com.example.application.queryservices;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.SchedulingId;
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
     * List All Booking Identifiers
     * @return List<SchedulingId>
     */
    public List<SchedulingId> findAllSchedulingIds(){

        return machineRepository.findAllSchedulingIds();
    }

    /**
     * Find a specific Cargo based on its Booking Id
     * @param schedulingId
     * @return Cargo
     */
    public Machine find(SchedulingId schedulingId){
        return machineRepository.findBySchedulingId(schedulingId);
    }
}
