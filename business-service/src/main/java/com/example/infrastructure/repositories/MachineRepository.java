package com.example.infrastructure.repositories;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository interface for Machine aggregate
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findByMachineId(MachineId machineId);

    List<MachineId> findAllMachineId();

    List<Machine> findAll();

    Job findJobByJobNumber(Integer jobNumber);

    Job findCurrentJobByMachineId(MachineId machineId);

    Machine findAllJobsByMachineId(MachineId machineId);

    Optional<Job> findJobInfoByJobNumber(Integer jobNumber);

    List<Job> findAllCustomerJobsByCustomerName(String customerName);

}
