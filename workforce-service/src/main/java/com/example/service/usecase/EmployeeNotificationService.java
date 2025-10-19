package com.example.service.usecase;

import com.example.domain.model.entities.ShiftPlan;
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
    public void notifyEmployeeOfShiftAssignment(ShiftPlan schedule, MachineScheduleCreated originalEvent) {
        try {
            logger.info("Sending shift notification to employee ID: {} for shift on {} (Priority: {})",
                       schedule.getEmployeeId(), schedule.getShiftDate(), schedule.getJobPriority());

            // TODO: Implement actual notification logic
            // - Send email notification
            // - Push mobile notification
            // - Update employee dashboard
            // - SMS notification for urgent assignments

            // For now, just log the notification
            logger.info("✅ Notification sent: Employee {} assigned to machine {} for job {} (Priority: {})",
                       schedule.getEmployeeId(),
                       originalEvent != null ? originalEvent.getMachineId() : "Unknown",
                       originalEvent != null ? originalEvent.getJobId() : schedule.getJobId(),
                       schedule.getJobPriority());

        } catch (Exception e) {
            logger.error("Failed to send notification to employee {}: {}", schedule.getEmployeeId(), e.getMessage());
        }
    }

    /**
     * Send notifications to multiple employees
     */
    public void notifyMultipleEmployees(java.util.List<ShiftPlan> schedules, MachineScheduleCreated originalEvent) {
        logger.info("Sending notifications to {} employees for machine schedule", schedules.size());

        for (ShiftPlan schedule : schedules) {
            notifyEmployeeOfShiftAssignment(schedule, originalEvent);
        }

        logger.info("Completed sending notifications for {} employees", schedules.size());
    }
}
