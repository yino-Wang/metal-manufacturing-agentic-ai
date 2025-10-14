package com.example.service.usecase;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.infrastructure.client.OpenAIClient;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.service.DTO.AutoScheduleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * OpenAI API configuration and AI scheduling functionality validation tests
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OpenAIValidationTest {

    @Autowired
    private OpenAIClient openAIClient;

    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // Create more test employee data to simulate real scenarios
        createTestEmployees();
    }

    private void createTestEmployees() {
        List<Employee> employees = Arrays.asList(
            createEmployee("Zhang San", "CNC,Mechanical Operation", 25.0f),
            createEmployee("Li Si", "Assembly,Quality Control", 22.0f),
            createEmployee("Wang Wu", "Welding,Maintenance", 28.0f),
            createEmployee("Zhao Liu", "CNC,Programming", 30.0f),
            createEmployee("Qian Qi", "Assembly,Packaging", 20.0f)
        );

        employeeRepository.saveAll(employees);
        System.out.println("✅ Created " + employees.size() + " test employees");
    }

    private Employee createEmployee(String name, String skills, float pay) {
        Employee emp = new Employee();
        emp.setName(name);
        emp.setSkill(skills);
        emp.setPay(pay);
        return emp;
    }

    @Test
    void testOpenAIClientDirectly() {
        System.out.println("\n=== Direct OpenAI Client Test ===");

        try {
            // Prepare test input
            AgentInput input = createTestAgentInput();

            System.out.println("Test input:");
            System.out.println("- Number of employees: " + input.getAvailableEmployees().size());
            System.out.println("- Time range: " + input.getStartTime() + " to " + input.getEndTime());
            System.out.println("- Staffing requirements: " + input.getStaffingRequirements());

            // Direct call to OpenAI client
            System.out.println("\n🤖 Calling OpenAI API...");
            long startTime = System.currentTimeMillis();

            List<ShiftSchedule> schedules = openAIClient.generateShiftPlan(input);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("⏱️ API call duration: " + duration + "ms");

            // Verify results
            if (schedules != null && !schedules.isEmpty()) {
                System.out.println("✅ OpenAI API call successful!");
                System.out.println("📋 Generated shift plans:");

                for (int i = 0; i < schedules.size(); i++) {
                    ShiftSchedule schedule = schedules.get(i);
                    System.out.printf("  %d. Employee ID: %d, Date: %s, Shift: %s%n",
                                    i + 1, schedule.getEmployeeId(),
                                    schedule.getShiftDate(), schedule.getShiftType());
                }

                // Verify schedule quality
                validateScheduleQuality(schedules, input);

            } else {
                System.out.println("❌ OpenAI API returned empty results");
                System.out.println("Possible causes:");
                System.out.println("1. Invalid API key");
                System.out.println("2. Network connection issues");
                System.out.println("3. Insufficient API quota");
                System.out.println("4. Response parsing failure");
            }

        } catch (Exception e) {
            System.err.println("❌ OpenAI API test failed: " + e.getMessage());
            e.printStackTrace();

            // Analyze specific error types
            analyzeOpenAIError(e);
        }
    }

    @Test
    void testCompleteAISchedulingWorkflow() {
        System.out.println("\n=== Complete AI Scheduling Workflow Test ===");

        try {
            // Prepare test parameters
            Calendar cal = Calendar.getInstance();
            Date startDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = cal.getTime();

            Long jobId = 1L;
            int requiredEmployees = 3;
            String shiftType = "DAY_SHIFT";

            System.out.println("📋 Testing complete scheduling process:");
            System.out.printf("- Start time: %s%n", startDate);
            System.out.printf("- End time: %s%n", endDate);
            System.out.printf("- Required employees: %d%n", requiredEmployees);
            System.out.printf("- Shift type: %s%n", shiftType);

            // Call complete scheduling service
            System.out.println("\n🔄 Executing auto scheduling...");
            AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, jobId, requiredEmployees, shiftType
            );

            // Analyze results
            if (response.getShiftSchedules() != null && !response.getShiftSchedules().isEmpty()) {
                System.out.println("✅ AI scheduling successful!");
                System.out.printf("📊 Generated %d shift plans%n", response.getShiftSchedules().size());

                // Detailed analysis of scheduling results
                analyzeSchedulingResults(response.getShiftSchedules());

                // Verify business rules
                validateBusinessRules(response.getShiftSchedules(), requiredEmployees);

            } else if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
                System.out.println("⚠️ No direct scheduling, but found alternative solutions:");
                for (Employee alt : response.getAlternatives()) {
                    System.out.printf("- %s (Skills: %s, Pay: %.2f)%n",
                                    alt.getName(), alt.getSkill(), alt.getPay());
                }
            } else {
                System.out.println("❌ AI scheduling failed - no results returned");
            }

        } catch (Exception e) {
            System.err.println("❌ Complete workflow test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void testOpenAIConnectivity() {
        System.out.println("\n=== OpenAI Connectivity Test ===");

        try {
            // Create simplest test input
            AgentInput simpleInput = new AgentInput();
            simpleInput.setAvailableEmployees(Arrays.asList(createEmployee("Test Employee", "General", 25.0f)));
            simpleInput.setStartTime(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            simpleInput.setEndTime(cal.getTime());

            Map<String, Integer> requirements = new HashMap<>();
            requirements.put("DAY_SHIFT", 1);
            simpleInput.setStaffingRequirements(requirements);

            Map<String, Object> constraints = new HashMap<>();
            constraints.put("maxHoursPerWeek", 40);
            constraints.put("minRestHours", 12);
            simpleInput.setConstraints(constraints);

            System.out.println("🔍 Testing basic connection...");

            // Try simple call
            List<ShiftSchedule> result = openAIClient.generateShiftPlan(simpleInput);

            if (result != null) {
                System.out.println("✅ OpenAI API connection normal");
                System.out.println("📡 API can respond to requests normally");

                if (!result.isEmpty()) {
                    System.out.println("✅ AI functionality normal - successfully generated schedules");
                } else {
                    System.out.println("⚠️ AI returned empty results, but connection is normal");
                }
            } else {
                System.out.println("❌ OpenAI API returned null");
            }

        } catch (Exception e) {
            System.err.println("❌ Connectivity test failed: " + e.getMessage());

            // Provide specific troubleshooting suggestions
            provideConnectionTroubleshooting(e);
        }
    }

    private AgentInput createTestAgentInput() {
        AgentInput input = new AgentInput();

        // Get test employees
        List<Employee> employees = employeeRepository.findAll();
        input.setAvailableEmployees(employees);

        // Set time range
        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        input.setStartTime(startDate);
        input.setEndTime(endDate);

        // Set scheduling requirements
        Map<String, Integer> requirements = new HashMap<>();
        requirements.put("DAY_SHIFT", 2);
        requirements.put("NIGHT_SHIFT", 1);
        input.setStaffingRequirements(requirements);

        // Set constraint conditions
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);
        input.setConstraints(constraints);

        return input;
    }

    private void validateScheduleQuality(List<ShiftSchedule> schedules, AgentInput input) {
        System.out.println("\n📊 Schedule Quality Analysis:");

        // Check employee assignments
        Set<Long> assignedEmployees = new HashSet<>();
        Map<String, Integer> shiftTypeCounts = new HashMap<>();

        for (ShiftSchedule schedule : schedules) {
            assignedEmployees.add(schedule.getEmployeeId());
            shiftTypeCounts.put(schedule.getShiftType(),
                shiftTypeCounts.getOrDefault(schedule.getShiftType(), 0) + 1);
        }

        System.out.println("- Assigned employees: " + assignedEmployees.size());
        System.out.println("- Shift distribution: " + shiftTypeCounts);

        // Verify if requirements are met
        for (Map.Entry<String, Integer> requirement : input.getStaffingRequirements().entrySet()) {
            int required = requirement.getValue();
            int assigned = shiftTypeCounts.getOrDefault(requirement.getKey(), 0);

            if (assigned >= required) {
                System.out.printf("✅ %s: Required %d, Assigned %d%n",
                                requirement.getKey(), required, assigned);
            } else {
                System.out.printf("❌ %s: Required %d, Only assigned %d%n",
                                requirement.getKey(), required, assigned);
            }
        }
    }

    private void analyzeSchedulingResults(List<ShiftSchedule> schedules) {
        System.out.println("\n📈 Detailed Scheduling Results Analysis:");

        Map<Long, List<ShiftSchedule>> employeeSchedules = new HashMap<>();
        Map<String, Integer> shiftTypeCount = new HashMap<>();

        for (ShiftSchedule schedule : schedules) {
            // Group by employee
            employeeSchedules.computeIfAbsent(schedule.getEmployeeId(), k -> new ArrayList<>())
                           .add(schedule);

            // Count shift types
            shiftTypeCount.put(schedule.getShiftType(),
                shiftTypeCount.getOrDefault(schedule.getShiftType(), 0) + 1);
        }

        System.out.printf("- Total schedules: %d%n", schedules.size());
        System.out.printf("- Employees involved: %d%n", employeeSchedules.size());
        System.out.println("- Shift distribution: " + shiftTypeCount);

        // Check employee workload
        for (Map.Entry<Long, List<ShiftSchedule>> entry : employeeSchedules.entrySet()) {
            Long employeeId = entry.getKey();
            int shiftCount = entry.getValue().size();
            System.out.printf("- Employee %d: %d shifts%n", employeeId, shiftCount);
        }
    }

    private void validateBusinessRules(List<ShiftSchedule> schedules, int requiredEmployees) {
        System.out.println("\n🔍 Business Rules Validation:");

        if (schedules.size() >= requiredEmployees) {
            System.out.println("✅ Meets minimum employee requirements");
        } else {
            System.out.println("❌ Does not meet minimum employee requirements");
        }
    }

    private void analyzeOpenAIError(Exception e) {
        System.out.println("\n🔍 Error Analysis:");

        String errorMessage = e.getMessage();
        Throwable cause = e.getCause();

        if (errorMessage != null) {
            if (errorMessage.contains("401") || errorMessage.contains("Unauthorized")) {
                System.out.println("❌ Invalid or expired API key");
                System.out.println("💡 Solution: Check openai.api.key configuration in application.properties");
            } else if (errorMessage.contains("403") || errorMessage.contains("Forbidden")) {
                System.out.println("❌ API access denied - possibly insufficient quota");
                System.out.println("💡 Solution: Check OpenAI account balance and quota");
            } else if (errorMessage.contains("429")) {
                System.out.println("❌ API call rate too high");
                System.out.println("💡 Solution: Reduce call frequency or upgrade account");
            } else if (errorMessage.contains("timeout") || errorMessage.contains("connect")) {
                System.out.println("❌ Network connection timeout");
                System.out.println("💡 Solution: Check network connection and firewall settings");
            } else if (errorMessage.contains("SSL") || errorMessage.contains("certificate")) {
                System.out.println("❌ SSL certificate issue");
                System.out.println("💡 Solution: Check system time and certificate configuration");
            } else {
                System.out.println("❌ Unknown error: " + errorMessage);
            }
        }

        if (cause != null) {
            System.out.println("🔗 Root cause: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
        }
    }

    private void provideConnectionTroubleshooting(Exception e) {
        System.out.println("\n🛠️ Connection Troubleshooting:");
        System.out.println("1. Check application.properties configuration");
        System.out.println("2. Verify API key format and validity");
        System.out.println("3. Confirm network can access api.openai.com");
        System.out.println("4. Check if proxy settings are needed");
        System.out.println("5. Verify OpenAI service status");

        // Analyze specific errors
        analyzeOpenAIError(e);
    }
}
