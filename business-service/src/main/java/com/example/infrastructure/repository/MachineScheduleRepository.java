package com.example.infrastructure.repository;

import com.example.model.MachineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineScheduleRepository extends JpaRepository<MachineSchedule, Long> {
}
