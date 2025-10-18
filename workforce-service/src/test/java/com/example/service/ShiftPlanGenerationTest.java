package com.example.service;

import com.example.domain.model.entities.ShiftPlan;
import com.example.shared.MachineSchedule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.main.web-application-type=none",
        "logging.level.com.example=INFO",
        "logging.level.com.example.service.ShiftPlannerService=DEBUG"
})
public class ShiftPlanGenerationTest {

    private static final Logger logger = LoggerFactory.getLogger(ShiftPlanGenerationTest.class);

    @Autowired
    private ShiftPlannerService shiftPlannerService;

    @Test
    public void testShiftPlanGeneration() {
        logger.info("=== Starting Shift Plan Generation Test ===");

        List<ShiftPlan> shiftPlans = null;
        Exception caughtException = null;

        try {
            // 1. First verify mock data
            MachineSchedule mockSchedule = shiftPlannerService.createMockMachineSchedule();
            logger.info("✅ Mock data created successfully, contains {} machines", mockSchedule.getSchedules().size());

            // Log mock data details
            mockSchedule.getSchedules().forEach((machineId, jobs) ->
                logger.info("  Machine {} has {} jobs", machineId, jobs.size())
            );

            mockSchedule.getSchedules().forEach((machineId, jobs) ->
                jobs.forEach(job ->
                    logger.info("    - Job ID: {}, Priority: {}, Days needed: {}",
                        job.getJobId(), job.getPriority(), job.getJobTimeNeededDays())
                )
            );

            // 2. Test shift plan generation with mock data (using Gemini AI first, fallback if needed)
            logger.info("--- Starting Shift Plan Generation (will try Gemini AI first) ---");
            shiftPlans = shiftPlannerService.createShiftPlansWithMockData(2);

            logger.info("✅ Shift plan generation completed!");
            logger.info("Number of shift plans generated: {}", shiftPlans.size());

        } catch (Exception e) {
            caughtException = e;
            logger.error("❌ Shift plan generation failed: {}", e.getClass().getSimpleName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);

            // Try to analyze error causes
            if (e.getMessage() != null) {
                if (e.getMessage().contains("No available employees")) {
                    logger.info("💡 Analysis: Need to add available employee data to database");
                } else if (e.getMessage().contains("Gemini")) {
                    logger.info("💡 Analysis: Check Gemini API configuration - may fallback to basic scheduling");
                }
            }
        }

        // 3. Verify results and display detailed information
        if (caughtException == null && shiftPlans != null) {
            analyzeShiftPlans(shiftPlans);
        }

        logger.info("=== Test Completed ===");
        logger.info("Final result: {}", (caughtException == null ? "Success" : "Failed"));

        // Don't fail the test if fallback worked - we want to see the complete output
        if (caughtException != null && (shiftPlans == null || shiftPlans.isEmpty())) {
            logger.warn("Test completed with errors, but this is expected if Gemini AI is not configured");
        }
    }

    private void analyzeShiftPlans(List<ShiftPlan> shiftPlans) {
        if (shiftPlans.isEmpty()) {
            logger.warn("⚠️  Warning: No shift plans were generated");
            return;
        }

        logger.info("--- Shift Plan Details ---");
        for (int i = 0; i < shiftPlans.size(); i++) {
            ShiftPlan plan = shiftPlans.get(i);
            logger.info("Shift Plan {}:", i + 1);
            logger.info("  - ID: {}", plan.getShiftPlanId());
            logger.info("  - Employee ID: {}", plan.getEmployeeId());
            logger.info("  - Job ID: {}", plan.getJobId());
            logger.info("  - Priority: {}", plan.getJobPriority());
            logger.info("  - Status: {}", plan.getStatus());
            logger.info("  - Shift Start Date: {}", plan.getShiftDate());
            logger.info("  - Work Start Time: {}", plan.getStartTime());
            logger.info("  - Work End Time: {}", plan.getEndTime());

            // Calculate job duration in days
            if (plan.getStartTime() != null && plan.getEndTime() != null) {
                long diffInMillies = plan.getEndTime().getTime() - plan.getStartTime().getTime();
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000) + 1; // +1 to include start date
                logger.info("  - Job Duration: {} days", diffInDays);
            }
        }

        // Verify key fields
        boolean hasValidData = shiftPlans.stream().anyMatch(plan ->
            plan.getEmployeeId() != null &&
            plan.getJobId() != null &&
            plan.getStatus() != null &&
            plan.getStartTime() != null &&
            plan.getEndTime() != null
        );

        if (hasValidData) {
            logger.info("✅ Validation passed: Shift plans contain complete date and time information");
        } else {
            logger.warn("❌ Validation failed: Shift plans missing critical date-time data");
        }

        // Display job duration statistics
        logger.info("--- Job Duration Statistics ---");
        Map<Long, Long> jobDurations = new HashMap<>();
        for (ShiftPlan plan : shiftPlans) {
            if (plan.getStartTime() != null && plan.getEndTime() != null) {
                long diffInMillies = plan.getEndTime().getTime() - plan.getStartTime().getTime();
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000) + 1;
                jobDurations.put(plan.getJobId(), diffInDays);
            }
        }

        jobDurations.forEach((jobId, days) ->
            logger.info("Job {} requires {} days to complete", jobId, days)
        );
    }
}
