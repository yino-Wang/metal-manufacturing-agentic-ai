package com.example.infrastructure.repositories;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.SchedulingId;
import com.example.domain.model.valueobjects.Job;
import com.example.domain.model.valueobjects.MachineName;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository interface for Machine aggregate
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findBySchedulingId(SchedulingId schedulingId);

    List<SchedulingId> findAllSchedulingId();

    List<Machine> findAll();

    Job findCurrentJobBySchedulingId(SchedulingId schedulingId);

    Machine findAllJobsBySchedulingId(SchedulingId schedulingId);

    Machine findByMachineName(MachineName machineName);
}
