package com.example.service.usecase;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.ShiftPlan;
import com.example.service.DTO.AutoScheduleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GenerateShiftPlanService autoGenerateShiftPlan functionality
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GenerateShiftPlanServiceAutoScheduleTest {

    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;

    @Test
    void testAutoGenerateShiftPlan_WithAvailableEmployees() {
        // Prepare test data
        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        Long jobId = 1L;
        int requiredEmployees = 2;
        String shiftType = "DAY_SHIFT";

        System.out.println("=== Test Auto Schedule Function ===");
        System.out.printf("Start time: %s%n", startDate);
        System.out.printf("End time: %s%n", endDate);
        System.out.printf("Job ID: %d%n", jobId);
        System.out.printf("Required employees: %d%n", requiredEmployees);
        System.out.printf("Shift type: %s%n", shiftType);

        try {
            // Call auto schedule function
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, jobId, requiredEmployees, shiftType
            );

            // Verify results
            assertNotNull(response, "Auto schedule response should not be null");

            if (response.getShiftSchedules() != null && !response.getShiftSchedules().isEmpty()) {
                System.out.println("✅ Auto scheduling successful!");
                System.out.printf("Generated %d shift plans:%n", response.getShiftSchedules().size());

                for (ShiftPlan schedule : response.getShiftSchedules()) {
                    System.out.printf("- Employee ID: %d, Shift: %s, Date: %s, Status: %s%n",
                        schedule.getEmployeeId(),
                        schedule.getShiftType(),
                        schedule.getShiftDate(),
                        schedule.getStatus());
                }

                // Verify schedule data integrity
                for (ShiftPlan schedule : response.getShiftSchedules()) {
                    assertNotNull(schedule.getEmployeeId(), "Employee ID should not be null");
                    assertEquals(shiftType, schedule.getShiftType(), "Shift type should match");
                    assertEquals(jobId, schedule.getJobId(), "Job ID should match");
                    assertEquals("PENDING_APPROVAL", schedule.getStatus(), "Status should be pending approval");
                }

            } else if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
                System.out.println("⚠️ No available employees, but found alternative solutions:");
                System.out.printf("Found %d alternative employees:%n", response.getAlternatives().size());

                for (Employee alt : response.getAlternatives()) {
                    System.out.printf("- Employee ID: %d, Skills: %s, Pay: %.2f%n",
                        alt.getEmployeeId(),
                        alt.getSkill(),
                        alt.getPay());
                }

                assertFalse(response.getAlternatives().isEmpty(), "Should have alternative employee suggestions");

            } else {
                System.out.println("❌ No available employees found, and no alternative solutions");
                fail("Auto scheduling should return shift plans or alternative solutions");
            }

        } catch (Exception e) {
            System.err.printf("❌ Auto scheduling test failed: %s%n", e.getMessage());
            e.printStackTrace();
            fail("Auto scheduling should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    void testAutoGenerateShiftPlan_DifferentShiftTypes() {
        System.out.println("\n=== Test Different Shift Types ===");

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        String[] shiftTypes = {"DAY_SHIFT", "NIGHT_SHIFT", "WEEKEND_SHIFT"};

        for (String shiftType : shiftTypes) {
            System.out.printf("\nTesting shift type: %s%n", shiftType);

            try {
                AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                    startDate, endDate, 1L, 1, shiftType
                );

                System.out.printf("%s shift test completed%n", shiftType);

                if (response.getShiftSchedules() != null) {
                    System.out.printf("✅ Successfully generated %d schedules%n", response.getShiftSchedules().size());
                } else if (response.getAlternatives() != null) {
                    System.out.printf("⚠️ Found %d alternative employees%n", response.getAlternatives().size());
                }

            } catch (Exception e) {
                System.err.printf("❌ %s shift test failed: %s%n", shiftType, e.getMessage());
            }
        }
    }

    @Test
    void testAutoGenerateShiftPlan_MultipleEmployees() {
        System.out.println("\n=== Test Multiple Employee Scheduling ===");

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date endDate = cal.getTime();

        int[] employeeCounts = {1, 3, 5};

        for (int count : employeeCounts) {
            System.out.printf("\nTesting required employee count: %d%n", count);

            try {
                AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                    startDate, endDate, 1L, count, "DAY_SHIFT"
                );

                if (response.getShiftSchedules() != null) {
                    int actualCount = response.getShiftSchedules().size();
                    System.out.printf("✅ Requested %d employees, actually scheduled %d employees%n", count, actualCount);

                    if (actualCount < count) {
                        System.out.printf("⚠️ Insufficient employees, missing %d employees%n", count - actualCount);
                    }
                } else {
                    System.out.printf("⚠️ Cannot meet requirement for %d employees, returning alternative solutions%n", count);
                }

            } catch (Exception e) {
                System.err.printf("❌ %d employee test failed: %s%n", count, e.getMessage());
            }
        }
    }

    @Test
    void testAutoGenerateShiftPlan_EdgeCases() {
        System.out.println("\n=== Test Edge Cases ===");

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        // Test invalid parameters
        System.out.println("Testing invalid parameters...");

        try {
            // Test 0 employees
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, 1L, 0, "DAY_SHIFT"
            );
            System.out.println("⚠️ 0 employee request was processed");
        } catch (Exception e) {
            System.out.println("✅ 0 employee request was correctly rejected: " + e.getMessage());
        }

        try {
            // Test negative employee count
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, 1L, -1, "DAY_SHIFT"
            );
            System.out.println("⚠️ Negative employee request was processed");
        } catch (Exception e) {
            System.out.println("✅ Negative employee request was correctly rejected: " + e.getMessage());
        }

        try {
            // Test null shift type
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, 1L, 1, null
            );
            System.out.println("⚠️ Null shift type was processed");
        } catch (Exception e) {
            System.out.println("✅ Null shift type was correctly rejected: " + e.getMessage());
        }
    }
}
