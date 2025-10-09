package com.example.infrastructure.repository;

import com.example.model.Employee;
import com.example.model.IndividualSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualScheduleRepository extends JpaRepository<IndividualSchedule, Long> {
    Employee findByEmployee(Employee employee);

    List<Employee> findAllEmployees();

    List<IndividualSchedule> findAll();
}
