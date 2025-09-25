package com.example.infrastructure.repository;
import com.example.domain.model.Timesheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByEmployee_EmployeeId(Long employeeId);
}
