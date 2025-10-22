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
            Long employeeId = schedule.getEmployeeId();
            String shiftDate = schedule.getShiftDate() != null ? schedule.getShiftDate().toString() : "Unknown";
            Integer jobPriority = schedule.getJobPriority();
            Long jobId = (originalEvent != null && originalEvent.getJobId() != null) ? originalEvent.getJobId() : schedule.getJobId();
            String machineId = extractMachineId(schedule, originalEvent);
            Long shiftPlanId = schedule.getShiftPlanId();
            int required = schedule.getRequiredEmployees();

            logger.info("Sending shift notification to employee ID: {} for shift on {} (Priority: {})",
                       employeeId, shiftDate, jobPriority);

            String title = "Shift Plan Assignment Notification";
            String body = String.format(
                "EmployeeID: %s\nShiftPlanID: %s\nShiftDate: %s\nMachine: %s\nJobID: %s\nPriority: %s\nrequiredEmployees: %d",
                employeeId, shiftPlanId, shiftDate, machineId, jobId, jobPriority != null ? jobPriority : "N/A", required
            );

            // print notification to terminal
            printTerminalNotification(title, body);

            logger.info("✅ Notification printed to terminal for employee {}", employeeId);

        } catch (Exception e) {
            logger.error("Failed to send notification to employee {}: {}", schedule.getEmployeeId(), e.getMessage(), e);
        }
    }

    private String extractMachineId(ShiftPlan schedule, MachineScheduleCreated originalEvent) {
        if (schedule != null) {
            try {
                java.lang.reflect.Method getter = schedule.getClass().getMethod("getMachineId");
                Object val = getter.invoke(schedule);
                if (val != null) {
                    // Normalize value without assuming it's a Long to avoid ClassCastException
                    return normalizeMachineIdObject(val);
                }
            } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException ignored) {

            }


            try {
                java.lang.reflect.Field field = schedule.getClass().getDeclaredField("machineId");
                field.setAccessible(true);
                Object val = field.get(schedule);
                if (val != null) {
                    // Normalize value without assuming it's a Long to avoid ClassCastException
                    return normalizeMachineIdObject(val);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {

            }
        }

        //
        if (originalEvent != null && originalEvent.getMachineId() != null) {
            return normalizeMachineIdObject(originalEvent.getMachineId());
        }

        return "Unknown";
    }

    // Helper to convert different machineId representations to a String
    private String normalizeMachineIdObject(Object val) {
        if (val instanceof String) {
            return (String) val;
        }
        if (val instanceof Number) {
            // keep existing formatting behavior for numeric ids
            return convertLongToMachineIdString(((Number) val).longValue());
        }
        // fallback to generic string conversion
        return String.valueOf(val);
    }

    /**
     * Convert Long machineId back to meaningful string representation
     * This reverses the conversion done in ShiftPlannerService
     */
    private String convertLongToMachineIdString(Long machineIdLong) {
        if (machineIdLong == null || machineIdLong == 0L) {
            return "Unknown";
        }

        // For IDs 1-999, convert to MACHINE-00X format
        if (machineIdLong >= 1 && machineIdLong <= 999) {
            return String.format("MACHINE-%03d", machineIdLong);
        }

        // For larger numbers, just return as is
        return "MACHINE-" + machineIdLong;
    }

    private void printTerminalNotification(String title, String body) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_CYAN = "\u001B[36m";
        String border = "────────────────────────────────────────";

        System.out.println(ANSI_GREEN + border);
        System.out.println(ANSI_YELLOW + " " + title);
        System.out.println(ANSI_CYAN + body);
        System.out.println(ANSI_GREEN + border + ANSI_RESET);
    }


    /**
     * Send notifications to multiple employees
     */
// java
    public  int notifyMultipleEmployees(java.util.List<ShiftPlan> schedules, MachineScheduleCreated originalEvent) {
        if (schedules == null || schedules.isEmpty()) {
            logger.info("No schedules to notify.");
            return 0;
        }

        logger.info("Sending notifications to {} employees for machine schedule", schedules.size());
        int processed = 0;

        for (ShiftPlan schedule : schedules) {
            try {
                notifyEmployeeOfShiftAssignment(schedule, originalEvent);
                processed++;
            } catch (Exception e) {

                logger.error("Failed to notify employee {} for shiftPlan {}: {}",
                        schedule != null ? schedule.getEmployeeId() : "unknown",
                        schedule != null ? schedule.getShiftPlanId() : "unknown",
                        e.getMessage(), e);
            }
        }

        logger.info("Completed sending notifications for {} employees (processed: {})", schedules.size(), processed);
        return processed;
    }}
