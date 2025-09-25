package com.example.infrastructure.repository;

import com.example.domain.model.IndividualSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualScheduleRepository extends JpaRepository<IndividualSchedule, Long> {
    List<IndividualSchedule> findByAssignedEmployee_EmployeeId(Long employeeId);
}
