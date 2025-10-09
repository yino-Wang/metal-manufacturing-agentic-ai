package com.example.infrastructure.repository;

import com.example.domain.model.Employee;
import com.example.domain.model.IndividualSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualScheduleRepository extends JpaRepository<IndividualSchedule, Long> {
    IndividualSchedule findByEmployee(Employee employee);

    List<Employee> findAllEmployees();

    List<IndividualSchedule> findAll();
}
