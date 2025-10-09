package com.example.infrastructure.repository;

import com.example.model.MachineSchedule;
import com.example.model.ScheduledJob;
import com.example.model.valueObjects.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineScheduleRepository extends JpaRepository<MachineSchedule, Long> {
    MachineSchedule findByMachine(Machine machine);

    List<Machine> findAllMachines();

    List<MachineSchedule> findAll();
}
