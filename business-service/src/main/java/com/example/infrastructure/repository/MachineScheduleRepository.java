package com.example.infrastructure.repository;

import com.example.domain.model.MachineSchedule;
import com.example.domain.model.valueObjects.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineScheduleRepository extends JpaRepository<MachineSchedule, Long> {
    MachineSchedule findByMachine(String machine);

    List<Machine> findAllMachines();

    List<MachineSchedule> findAll();
}
