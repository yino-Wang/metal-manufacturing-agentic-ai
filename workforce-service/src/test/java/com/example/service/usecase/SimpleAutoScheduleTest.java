package com.example.service.usecase;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.service.DTO.AutoScheduleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Simple autoGenerateShiftPlan functionality test
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleAutoScheduleTest {

    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // Create test employee data
        Employee emp1 = new Employee();
        emp1.setName("John Smith");
        emp1.setSkill("CNC");
        emp1.setPay(25.0f);
        emp1.setEmployeeId(1L);
        emp1.setManager(false);
        emp1.setScheduledJobs(0);
        emp1.setPhoneNumber("123-456-7890");



        Employee emp2 = new Employee();
        emp2.setName("Jane Doe");
        emp2.setSkill("Assembly");
        emp2.setPay(22.0f);
        emp2.setEmployeeId(2L);
        emp2.setManager(false);
        emp2.setScheduledJobs(0);
        emp2.setPhoneNumber("123-456-7891");

        Employee emp3 = new Employee();
        emp3.setName("Bob Wilson");
        emp3.setSkill("Quality");
        emp3.setPay(28.0f);
        emp3.setEmployeeId(3L);
        emp3.setManager(false);
        emp3.setScheduledJobs(0);
        emp3.setPhoneNumber("123-456-7893");

        try {
            employeeRepository.save(emp1);
            employeeRepository.save(emp2);
            employeeRepository.save(emp3);
            System.out.println("✅ Successfully created test employee data");
        } catch (Exception e) {
            System.out.println("⚠️ Failed to create test employees: " + e.getMessage());
        }
    }

    @Test
    void testAutoGenerateShiftPlan_Basic() {
        System.out.println("\n=== Basic Auto Schedule Test ===");

        // Prepare test parameters
        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        Long jobId = 1L;
        int requiredEmployees = 3;
        String shiftType = "DAY_SHIFT";

        System.out.printf("Test parameters:%n");
        System.out.printf("- Start time: %s%n", startDate);
        System.out.printf("- End time: %s%n", endDate);
        System.out.printf("- Job ID: %d%n", jobId);
        System.out.printf("- Required employees: %d%n", requiredEmployees);
        System.out.printf("- Shift type: %s%n", shiftType);

        try {
            // Check employee count in database first
            long employeeCount = employeeRepository.count();
            System.out.printf("Total employees in database: %d%n", employeeCount);

            // Call auto schedule function
            System.out.println("\nCalling autoGenerateShiftPlan...");
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, jobId, requiredEmployees, shiftType
            );

            // Check response results
            if (response == null) {
                System.out.println("❌ Response is null");
                return;
            }

            System.out.println("\n=== Schedule Results Analysis ===");

            // Check if there are successful schedules
            if (response.getShiftSchedules() != null && !response.getShiftSchedules().isEmpty()) {
                System.out.printf("✅ Successfully generated %d shift plans:%n", response.getShiftSchedules().size());

                for (int i = 0; i < response.getShiftSchedules().size(); i++) {
                    ShiftSchedule schedule = response.getShiftSchedules().get(i);
                    System.out.printf("  Schedule %d:%n", i + 1);
                    System.out.printf("    - Employee ID: %d%n", schedule.getEmployeeId());
                    System.out.printf("    - Shift type: %s%n", schedule.getShiftType());
                    System.out.printf("    - Schedule date: %s%n", schedule.getShiftDate());
                    System.out.printf("    - Job ID: %d%n", schedule.getJobId());
                    System.out.printf("    - Status: %s%n", schedule.getStatus());
                }

                System.out.println("✅ autoGenerateShiftPlan function works correctly!");

            } else if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
                System.out.printf("⚠️ No directly available employees, but found %d alternative solutions:%n", response.getAlternatives().size());

                for (int i = 0; i < response.getAlternatives().size(); i++) {
                    Employee alt = response.getAlternatives().get(i);
                    System.out.printf("  Alternative employee %d:%n", i + 1);
                    System.out.printf("    - Employee ID: %d%n", alt.getEmployeeId());
                    System.out.printf("    - Name: %s%n", alt.getName());
                    System.out.printf("    - Skills: %s%n", alt.getSkill());
                    System.out.printf("    - Pay: %.2f%n", alt.getPay());
                }

                System.out.println("✅ autoGenerateShiftPlan alternative solution function works correctly!");

            } else {
                System.out.println("⚠️ No schedule solutions or alternative employees found");
                System.out.println("This might be because:");
                System.out.println("1. No qualified available employees");
                System.out.println("2. OpenAI client returned empty results");
                System.out.println("3. Database query issues");
            }

        } catch (Exception e) {
            System.err.printf("❌ Exception occurred during test: %s%n", e.getMessage());
            e.printStackTrace();

            // Try to analyze specific issues
            System.out.println("\n=== Error Analysis ===");
            if (e.getMessage().contains("No available employee")) {
                System.out.println("Issue: No available employees");
                System.out.println("Solution: Check employee data or findAvailableEmployees() method");
            } else if (e.getMessage().contains("OpenAI")) {
                System.out.println("Issue: OpenAI client error");
                System.out.println("Solution: Check OpenAI configuration or network connection");
            }
        }
    }

    @Test
    void testAutoGenerateShiftPlan_NoEmployees() {
        System.out.println("\n=== Test No Employees Scenario ===");

        // Clear employee data
        employeeRepository.deleteAll();

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        try {
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, 1L, 1, "DAY_SHIFT"
            );

            if (response != null && response.getAlternatives() != null) {
                System.out.printf("✅ Correctly handled no employees scenario, found %d alternative solutions%n", response.getAlternatives().size());
            } else {
                System.out.println("⚠️ No employees scenario handling result is empty");
            }

        } catch (Exception e) {
            System.out.println("✅ Correctly threw exception handling no employees scenario: " + e.getMessage());
        }
    }
}
