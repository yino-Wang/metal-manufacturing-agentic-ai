package com.example.service.queryservice;

import com.example.domain.model.entities.ShiftSchedule;
import com.example.infrastructure.repository.ShiftPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * QueryService for shift plan related queries.
 * Only handles read/query operations for ShiftSchedule.
 */
@Service
public class ShiftPlanQueryService {
    private final ShiftPlanRepository shiftPlanRepository;

    @Autowired
    public ShiftPlanQueryService(ShiftPlanRepository shiftPlanRepository) {
        this.shiftPlanRepository = shiftPlanRepository;
    }

    /**
     * Find all shift schedules for a given employee.
     */
    public List<ShiftSchedule> findByEmployeeId(Integer employeeId) {
        return shiftPlanRepository.findByEmployeeId(employeeId);
    }

    /**
     * Find all shift schedules for a given date.
     */
    public List<ShiftSchedule> findByShiftDate(Date shiftDate) {
        return shiftPlanRepository.findByShiftDate(shiftDate);
    }

    /**
     * Find all shift schedules.
     */
    public List<ShiftSchedule> findAll() {
        return shiftPlanRepository.findAll();
    }
}
