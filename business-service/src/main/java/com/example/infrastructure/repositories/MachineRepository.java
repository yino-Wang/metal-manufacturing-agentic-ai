package com.example.infrastructure.repositories;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Job;
import com.example.domain.model.valueobjects.MachineName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository interface for Machine aggregate
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findByMachineId(MachineId machineId);

    List<MachineId> findAllMachineId();

    List<Machine> findAll();

    Job findCurrentJobByMachineId(MachineId machineId);

    Machine findAllJobsByMachineId(MachineId machineId);

}
