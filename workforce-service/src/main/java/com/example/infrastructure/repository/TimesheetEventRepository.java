package com.example.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.domain.event.TimesheetEvent;
import java.util.Date;
import java.util.List;

public interface TimesheetEventRepository extends JpaRepository<TimesheetEvent, Long> {
    /**
     * Find all TimesheetEvent by employeeId
     */
    List<TimesheetEvent> findByTimesheet_EmployeeId(Long employeeId);

    /**
     * Find all TimesheetEvent by workDate
     */
    List<TimesheetEvent> findByTimesheet_WorkDate(Date workDate);
}
