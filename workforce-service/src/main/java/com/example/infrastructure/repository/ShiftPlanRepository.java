package com.example.infrastructure.repository;

import com.example.domain.model.ShiftPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftPlanRepository extends JpaRepository<ShiftPlan,Long> {
    List<ShiftPlan> findByEmployeeId(Integer employeeId);
}
