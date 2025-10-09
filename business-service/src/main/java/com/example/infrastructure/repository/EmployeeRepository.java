package com.example.infrastructure.repository;

import com.example.domain.model.Employee;
import com.example.domain.model.IndividualSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findById(Long id);

    List<Employee> findAll();

}