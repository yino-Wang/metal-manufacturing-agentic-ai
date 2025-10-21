package com.example.infrastructure.repository;
import com.example.domain.model.aggregates.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.status = 'AVAILABLE'")
    List<Employee> findAvailableEmployees();
}
