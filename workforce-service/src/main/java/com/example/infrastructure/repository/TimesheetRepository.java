package com.example.infrastructure.repository;
import com.example.domain.model.entities.Timesheet;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByEmployee_EmployeeId(Long employeeId);

    List<Timesheet> findByWorkDate(Date workDate);
}
