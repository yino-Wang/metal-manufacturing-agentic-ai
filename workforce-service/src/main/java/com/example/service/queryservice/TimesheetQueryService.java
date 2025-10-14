package com.example.service.queryservice;

import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * QueryService for timesheet related queries.
 * Only handles read/query operations for Timesheet.
 */
@Service
public class TimesheetQueryService {
    private final TimesheetRepository timesheetRepository;

    @Autowired
    public TimesheetQueryService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    /**
     * Find all timesheets for a given employee.
     */
    public List<Timesheet> findByEmployeeId(Long employeeId) {
        return timesheetRepository.findByEmployee_EmployeeId(employeeId);
    }

    /**
     * Find all timesheets for a given date.
     */
    public List<Timesheet> findByWorkDate(Date workDate) {
        return timesheetRepository.findByWorkDate(workDate);
    }

    /**
     * Find all timesheets.
     */
    public List<Timesheet> findAll() {
        return timesheetRepository.findAll();
    }
}
