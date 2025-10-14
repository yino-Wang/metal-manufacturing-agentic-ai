package com.example.infrastructure.repository;

import com.example.domain.model.entities.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ShiftPlanRepository extends JpaRepository<ShiftSchedule,Long> {
    List<ShiftSchedule> findByEmployeeId(Long employeeId);

    List<ShiftSchedule> findByShiftDate(Date shiftDate);
}
