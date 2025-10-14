package com.example.service.usecase;

import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.event.MachineScheduleCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * EmployeeNotificationService
 * Handles notifications to employees about their shift assignments
 */
@Service
public class EmployeeNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeNotificationService.class);

    /**
     * Send shift assignment notification to employee
     */
    public void notifyEmployeeOfShiftAssignment(ShiftSchedule schedule, MachineScheduleCreated originalEvent) {
        try {
            logger.info("Sending shift notification to employee ID: {} for shift on {} ({})",
                       schedule.getEmployeeId(), schedule.getShiftDate(), schedule.getShiftType());

            // TODO: Implement actual notification logic
            // - Send email notification
            // - Push mobile notification
            // - Update employee dashboard
            // - SMS notification for urgent assignments

            // For now, just log the notification
            logger.info("Notification sent: Employee {} assigned to {} shift on {} for machine {}",
                       schedule.getEmployeeId(), schedule.getShiftType(),
                       schedule.getShiftDate(), originalEvent.getMachineId());

        } catch (Exception e) {
            logger.error("Failed to send notification to employee {}: {}", schedule.getEmployeeId(), e.getMessage());
        }
    }

    /**
     * Send batch notifications for multiple shift assignments
     */
    public void notifyMultipleEmployees(java.util.List<ShiftSchedule> schedules, MachineScheduleCreated originalEvent) {
        for (ShiftSchedule schedule : schedules) {
            notifyEmployeeOfShiftAssignment(schedule, originalEvent);
        }
    }
}
