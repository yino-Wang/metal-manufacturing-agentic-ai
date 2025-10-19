package com.example.infrastructure.repository;

import com.example.domain.model.entities.ShiftPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ShiftPlanRepository extends JpaRepository<ShiftPlan,Long> {
    List<ShiftPlan> findByEmployeeId(Long employeeId);

    List<ShiftPlan> findByShiftDate(Date shiftDate);
}
