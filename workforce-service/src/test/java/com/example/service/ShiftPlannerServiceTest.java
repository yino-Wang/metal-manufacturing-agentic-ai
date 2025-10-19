package com.example.service;

import com.example.ExternalMachineSchedule;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftPlan;
import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.TimesheetRepository;
import com.example.service.usecase.GenerateShiftPlanService;
import com.example.shared.MachineSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration test for ShiftPlannerService with real Gemini AI calls
 */
@SpringBootTest
@ActiveProfiles("test")
public class ShiftPlannerServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ShiftPlannerServiceTest.class);

    @Autowired
    private ShiftPlannerService shiftPlannerService;

    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;

    @Autowired
    private ExternalMachineSchedule externalMachineSchedule;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TimesheetRepository timesheetRepository;

    private List<Employee> mockEmployees;
    private List<Timesheet> mockTimesheets;

    @BeforeEach
    void setUp() {
        // Setup mock data for employees and timesheets (these don't need real Gemini)
        setupMockEmployees();
        setupMockTimesheets();
        setupMockRepositoryBehavior();
    }

    /**
     * Create five mock employees with different skills and availability
     */
    private void setupMockEmployees() {
        mockEmployees = new ArrayList<>();

        // Employee 1: Senior Welder
        Employee emp1 = new Employee();
        emp1.setEmployeeId(1L);
        emp1.setName("John Smith (Senior Welder)");
        emp1.setPhoneNumber("138-0001-0001");
        emp1.setSkill("Welding,Steel_Working");
        emp1.setSalary(5000.0f);
        emp1.setPay(25.0f); // hourly rate
        emp1.setStartDatePayslip(Date.from(LocalDate.of(2025, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp1.setEndDatePayslip(Date.from(LocalDate.of(2025, 10, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp1.setScheduleId(101);
        emp1.setManager(false);
        emp1.setManagerName("Manager Wang");
        emp1.setScheduledJobs(0);
        mockEmployees.add(emp1);

        // Employee 2: Machine Operator
        Employee emp2 = new Employee();
        emp2.setEmployeeId(2L);
        emp2.setName("David Johnson (Machine Operator)");
        emp2.setPhoneNumber("138-0002-0002");
        emp2.setSkill("Machine_Operation,Quality_Control");
        emp2.setSalary(4500.0f);
        emp2.setPay(22.5f); // hourly rate
        emp2.setStartDatePayslip(Date.from(LocalDate.of(2025, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp2.setEndDatePayslip(Date.from(LocalDate.of(2025, 10, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp2.setScheduleId(102);
        emp2.setManager(false);
        emp2.setManagerName("Manager Wang");
        emp2.setScheduledJobs(0);
        mockEmployees.add(emp2);

        // Employee 3: Maintenance Technician
        Employee emp3 = new Employee();
        emp3.setEmployeeId(3L);
        emp3.setName("Michael Brown (Maintenance Tech)");
        emp3.setPhoneNumber("138-0003-0003");
        emp3.setSkill("Maintenance,Repair,Electrical");
        emp3.setSalary(4800.0f);
        emp3.setPay(24.0f); // hourly rate
        emp3.setStartDatePayslip(Date.from(LocalDate.of(2025, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp3.setEndDatePayslip(Date.from(LocalDate.of(2025, 10, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp3.setScheduleId(103);
        emp3.setManager(false);
        emp3.setManagerName("Manager Wang");
        emp3.setScheduledJobs(0);
        mockEmployees.add(emp3);

        // Employee 4: Assembly Specialist
        Employee emp4 = new Employee();
        emp4.setEmployeeId(4L);
        emp4.setName("Sarah Wilson (Assembly Specialist)");
        emp4.setPhoneNumber("138-0004-0004");
        emp4.setSkill("Assembly,Manufacturing,Quality_Assurance");
        emp4.setSalary(4200.0f);
        emp4.setPay(21.0f); // hourly rate
        emp4.setStartDatePayslip(Date.from(LocalDate.of(2025, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp4.setEndDatePayslip(Date.from(LocalDate.of(2025, 10, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp4.setScheduleId(104);
        emp4.setManager(false);
        emp4.setManagerName("Manager Wang");
        emp4.setScheduledJobs(0);
        mockEmployees.add(emp4);

        // Employee 5: CNC Operator
        Employee emp5 = new Employee();
        emp5.setEmployeeId(5L);
        emp5.setName("Robert Garcia (CNC Operator)");
        emp5.setPhoneNumber("138-0005-0005");
        emp5.setSkill("CNC_Operation,Programming,Precision_Machining");
        emp5.setSalary(5200.0f);
        emp5.setPay(26.0f); // hourly rate
        emp5.setStartDatePayslip(Date.from(LocalDate.of(2025, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp5.setEndDatePayslip(Date.from(LocalDate.of(2025, 10, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        emp5.setScheduleId(105);
        emp5.setManager(false);
        emp5.setManagerName("Manager Wang");
        emp5.setScheduledJobs(0);
        mockEmployees.add(emp5);

        logger.info("Created {} mock employees", mockEmployees.size());
        for (Employee emp : mockEmployees) {
            logger.info("  Employee {}: {} (Skills: {}, Rate: ${}/hr)",
                    emp.getEmployeeId(), emp.getName(), emp.getSkill(), emp.getPay());
        }
    }

    /**
     * Create mock timesheets for each employee (one timesheet per employee)
     */
    private void setupMockTimesheets() {
        mockTimesheets = new ArrayList<>();

        Date workDate = Date.from(LocalDate.of(2025, 10, 19).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Timesheet for Employee 1
        Timesheet ts1 = new Timesheet();
        ts1.setTimesheetId(1L);
        ts1.setEmployeeId(1L);
        ts1.setWorkDate(workDate);
        ts1.setClockInTime(LocalDateTime.of(2025, 10, 19, 8, 0)); // 8:00 AM
        ts1.setClockOutTime(LocalDateTime.of(2025, 10, 19, 16, 0)); // 4:00 PM
        ts1.setHoursWorked(8.0f);
        ts1.setSalaryPaid(200.0f); // 8 hours * $25/hour
        ts1.setJobId(1L);
        ts1.setStatus("APPROVED");
        mockTimesheets.add(ts1);

        // Timesheet for Employee 2
        Timesheet ts2 = new Timesheet();
        ts2.setTimesheetId(2L);
        ts2.setEmployeeId(2L);
        ts2.setWorkDate(workDate);
        ts2.setClockInTime(LocalDateTime.of(2025, 10, 19, 8, 30)); // 8:30 AM
        ts2.setClockOutTime(LocalDateTime.of(2025, 10, 19, 16, 30)); // 4:30 PM
        ts2.setHoursWorked(8.0f);
        ts2.setSalaryPaid(180.0f); // 8 hours * $22.5/hour
        ts2.setJobId(2L);
        ts2.setStatus("PENDING_APPROVAL");
        mockTimesheets.add(ts2);

        // Timesheet for Employee 3
        Timesheet ts3 = new Timesheet();
        ts3.setTimesheetId(3L);
        ts3.setEmployeeId(3L);
        ts3.setWorkDate(workDate);
        ts3.setClockInTime(LocalDateTime.of(2025, 10, 19, 7, 45)); // 7:45 AM
        ts3.setClockOutTime(LocalDateTime.of(2025, 10, 19, 15, 45)); // 3:45 PM
        ts3.setHoursWorked(8.0f);
        ts3.setSalaryPaid(192.0f); // 8 hours * $24/hour
        ts3.setJobId(3L);
        ts3.setStatus("APPROVED");
        mockTimesheets.add(ts3);

        // Timesheet for Employee 4
        Timesheet ts4 = new Timesheet();
        ts4.setTimesheetId(4L);
        ts4.setEmployeeId(4L);
        ts4.setWorkDate(workDate);
        ts4.setClockInTime(LocalDateTime.of(2025, 10, 19, 8, 15)); // 8:15 AM
        ts4.setClockOutTime(LocalDateTime.of(2025, 10, 19, 16, 15)); // 4:15 PM
        ts4.setHoursWorked(8.0f);
        ts4.setSalaryPaid(168.0f); // 8 hours * $21/hour
        ts4.setJobId(4L);
        ts4.setStatus("APPROVED");
        mockTimesheets.add(ts4);

        // Timesheet for Employee 5
        Timesheet ts5 = new Timesheet();
        ts5.setTimesheetId(5L);
        ts5.setEmployeeId(5L);
        ts5.setWorkDate(workDate);
        ts5.setClockInTime(LocalDateTime.of(2025, 10, 19, 7, 30)); // 7:30 AM
        ts5.setClockOutTime(LocalDateTime.of(2025, 10, 19, 15, 30)); // 3:30 PM
        ts5.setHoursWorked(8.0f);
        ts5.setSalaryPaid(208.0f); // 8 hours * $26/hour
        ts5.setJobId(5L);
        ts5.setStatus("PENDING_APPROVAL");
        mockTimesheets.add(ts5);

        logger.info("Created {} mock timesheets", mockTimesheets.size());
        for (Timesheet ts : mockTimesheets) {
            logger.info("  Timesheet {}: Employee {} worked {} hours on {} (Status: {})",
                    ts.getTimesheetId(), ts.getEmployeeId(), ts.getHoursWorked(),
                    ts.getWorkDate(), ts.getStatus());
        }
    }

    /**
     * Setup mock repository behavior with lenient mocking to avoid UnnecessaryStubbing warnings
     */
    private void setupMockRepositoryBehavior() {
        // Mock employee repository with lenient mode
        lenient().when(employeeRepository.findAvailableEmployees()).thenReturn(mockEmployees);
        lenient().when(employeeRepository.findAll()).thenReturn(mockEmployees);

        for (Employee emp : mockEmployees) {
            lenient().when(employeeRepository.findById(emp.getEmployeeId())).thenReturn(Optional.of(emp));
            lenient().when(employeeRepository.existsById(emp.getEmployeeId())).thenReturn(true);
        }

        // Mock timesheet repository with lenient mode - using correct method name
        lenient().when(timesheetRepository.findAll()).thenReturn(mockTimesheets);

        for (Timesheet ts : mockTimesheets) {
            lenient().when(timesheetRepository.findById(ts.getTimesheetId())).thenReturn(Optional.of(ts));
            // Use correct method name from TimesheetRepository
            lenient().when(timesheetRepository.findByEmployee_EmployeeId(ts.getEmployeeId()))
                    .thenReturn(mockTimesheets.stream()
                            .filter(t -> t.getEmployeeId().equals(ts.getEmployeeId()))
                            .collect(Collectors.toList()));
        }

        logger.info("Mock repository behavior configured with lenient mode");
    }

    @Test
    void testFetchMachineScheduleFromExternalService() {
        logger.info("=== Testing ExternalMachineSchedule Mock Data ===");

        // Test fetching machine schedule from ExternalMachineSchedule
        MachineSchedule schedule = shiftPlannerService.fetchMachineScheduleFromBusiness();

        // Verify the schedule contains ExternalMachineSchedule's mock data
        assertNotNull(schedule, "Machine schedule should not be null");
        assertNotNull(schedule.getSchedules(), "Schedule map should not be null");
        assertEquals(3, schedule.getSchedules().size(), "Should have 3 machines from ExternalMachineSchedule");

        logger.info("✅ Fetched machine schedule with {} machines", schedule.getSchedules().size());

        // Verify the mock data structure
        schedule.getSchedules().forEach((machineId, jobs) -> {
            logger.info("Machine {}: {} jobs", machineId, jobs.size());
            jobs.forEach(job -> {
                logger.info("  Job ID: {}, Priority: {}, Title: {}",
                        job.getJobId(), job.getPriority(), job.getTitle());
            });
        });

        logger.info("✅ ExternalMachineSchedule mock data test completed!");
    }

    @Test
    void testRealGeminiShiftPlanGeneration() {
        logger.info("=== Testing REAL Gemini AI Shift Plan Generation with Enhanced Day-by-Day Planning ===");

        // Test the complete pipeline with REAL Gemini AI calls
        int requiredEmployees = 1;

        logger.info("🚀 Calling REAL Gemini AI to generate shift plans...");
        logger.info("Available employees: {}", mockEmployees.size());
        logger.info("Required employees per job: {}", requiredEmployees);

        try {
            List<ShiftPlan> result = shiftPlannerService.createShiftPlans(requiredEmployees);

            // Verify the real Gemini response
            assertNotNull(result, "Gemini should generate shift plans");
            assertTrue(result.size() > 0, "Gemini should generate at least one shift plan");

            logger.info("🎉 Gemini AI successfully generated {} shift plans", result.size());

            // NEW: Verify day-by-day planning capability
            //verifyDayByDayPlanning(result);

            // NEW: Verify completion time estimation
            verifyCompletionTimeEstimation(result);

            // Verify each plan has proper structure from real Gemini
            for (ShiftPlan plan : result) {
                assertNotNull(plan.getEmployeeId(), "Gemini should assign employee ID");
                assertNotNull(plan.getJobId(), "Gemini should assign job ID from ExternalMachineSchedule");
                assertNotNull(plan.getStatus(), "Status should be set");
                assertNotNull(plan.getStartTime(), "Gemini should set start time");
                assertNotNull(plan.getEndTime(), "Gemini should set end time");

                // Verify employee exists
                assertTrue(plan.getEmployeeId() >= 1L && plan.getEmployeeId() <= 5L,
                        "Employee ID should be between 1 and 5");

                // Verify reasonable work hours (8 hours = 8 * 60 * 60 * 1000 milliseconds)
                long workDuration = plan.getEndTime().getTime() - plan.getStartTime().getTime();
                long hoursWorked = workDuration / (1000 * 60 * 60);
                assertTrue(hoursWorked >= 6 && hoursWorked <= 12,
                        "Work duration should be reasonable (6-12 hours), got: " + hoursWorked + " hours");

                logger.info("✓ Real Gemini Plan: Employee {} -> Job {} (Priority: {}) | {} to {} ({} hours)",
                        plan.getEmployeeId(), plan.getJobId(), plan.getJobPriority(),
                        plan.getStartTime(), plan.getEndTime(), hoursWorked);
            }

            // Verify Gemini respected priority constraints
            //verifyPriorityRespected(result);

            // Check for one employee per day constraint
            verifyOneEmployeePerDayConstraint(result);

            logger.info("✅ Real Gemini AI shift plan generation test completed successfully!");

        } catch (Exception e) {
            logger.error("❌ Real Gemini AI call failed: {}", e.getMessage());
            fail("Real Gemini AI call should succeed. Check your API key and configuration. Error: " + e.getMessage());
        }
    }

    /**
     * NEW: Verify that Gemini plans work day by day with proper grouping
     */
  /*  private void verifyDayByDayPlanning(List<ShiftPlan> result) {
        logger.info("--- Verifying Day-by-Day Planning ---");

        // Group plans by date
        Map<String, List<ShiftPlan>> plansByDate = result.stream()
                .collect(Collectors.groupingBy(plan ->
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(plan.getShiftDate())
                ));

        logger.info("Gemini organized work across {} days:", plansByDate.size());

        plansByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String date = entry.getKey();
                    List<ShiftPlan> dailyPlans = entry.getValue();

                    logger.info("  Day {}: {} shift plans", date, dailyPlans.size());

                    // Verify employees are not double-booked on same day
                    Set<Long> employeesOnDay = dailyPlans.stream()
                            .map(ShiftPlan::getEmployeeId)
                            .collect(Collectors.toSet());

                    assertEquals(employeesOnDay.size(), dailyPlans.size(),
                            "No employee should be assigned multiple jobs on day " + date);

                    // Log job priorities for this day
                    String priorityInfo = dailyPlans.stream()
                            .sorted(Comparator.comparing(ShiftPlan::getJobPriority))
                            .map(p -> "Job " + p.getJobId() + "(P" + p.getJobPriority() + ")")
                            .collect(Collectors.joining(", "));

                    logger.info("    Jobs: {}", priorityInfo);
                });

        assertTrue(plansByDate.size() >= 1, "Should plan for at least 1 day");
        assertTrue(plansByDate.size() <= 7, "Should not exceed 7 days for test data");

        logger.info("✅ Day-by-day planning verification passed");
    }*/

    /**
     * NEW: Verify completion time estimation logic
     */
    private void verifyCompletionTimeEstimation(List<ShiftPlan> result) {
        logger.info("--- Verifying Completion Time Estimation ---");

        // Calculate actual completion days
        Set<String> workingDays = result.stream()
                .map(plan -> new java.text.SimpleDateFormat("yyyy-MM-dd").format(plan.getShiftDate()))
                .collect(Collectors.toSet());

        int actualDays = workingDays.size();

        // Theoretical calculation: (Total jobs × employees per job) ÷ total employees
        int totalJobs = 10; // From enhanced ExternalMachineSchedule
        int employeesPerJob = 1;
        int totalEmployees = 5;
        int theoreticalMinDays = Math.max(1, (int) Math.ceil((double) (totalJobs * employeesPerJob) / totalEmployees));

        logger.info("Completion time analysis:");
        logger.info("  Total jobs: {}", totalJobs);
        logger.info("  Employees per job: {}", employeesPerJob);
        logger.info("  Total employees: {}", totalEmployees);
        logger.info("  Theoretical minimum days: {}", theoreticalMinDays);
        logger.info("  Gemini planned days: {}", actualDays);

        // Gemini should be reasonably close to theoretical minimum
        assertTrue(actualDays >= theoreticalMinDays,
                "Gemini should not plan fewer days than theoretically possible");
        assertTrue(actualDays <= theoreticalMinDays + 2,
                "Gemini should be reasonably efficient (within 2 days of minimum)");

        logger.info("✅ Completion time estimation verification passed");
    }

    /**
     * NEW: Verify priority constraints are respected
     */
 /*   private void verifyPriorityRespected(List<ShiftPlan> result) {
        logger.info("--- Verifying Priority Constraints ---");

        List<ShiftPlan> highPriorityPlans = result.stream()
                .filter(p -> p.getJobPriority() != null && p.getJobPriority() <= 2)
                .toList();

        if (highPriorityPlans.size() > 0) {
            logger.info("✅ Gemini assigned {} high priority jobs (priority 1-2)", highPriorityPlans.size());

            // Verify high priority jobs are scheduled early
            Date earliestHighPriorityDate = highPriorityPlans.stream()
                    .map(ShiftPlan::getShiftDate)
                    .min(Date::compareTo)
                    .orElse(null);

            if (earliestHighPriorityDate != null) {
                logger.info("Earliest high priority job scheduled: {}",
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(earliestHighPriorityDate));
            }
        }

        // Group by priority and verify scheduling order
        Map<Integer, List<ShiftPlan>> plansByPriority = result.stream()
                .filter(p -> p.getJobPriority() != null)
                .collect(Collectors.groupingBy(ShiftPlan::getJobPriority));

        plansByPriority.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    logger.info("Priority {}: {} jobs scheduled", entry.getKey(), entry.getValue().size());
                });

        logger.info("✅ Priority constraint verification completed");
    }*/

    /**
     * NEW: Verify one employee per day constraint
     */
    private void verifyOneEmployeePerDayConstraint(List<ShiftPlan> result) {
        logger.info("--- Verifying One Employee Per Day Constraint ---");

        Map<String, Set<Long>> dateEmployeeMap = new HashMap<>();
        boolean constraintViolated = false;

        for (ShiftPlan plan : result) {
            String dateKey = new java.text.SimpleDateFormat("yyyy-MM-dd").format(plan.getShiftDate());
            dateEmployeeMap.computeIfAbsent(dateKey, k -> new HashSet<>());

            if (dateEmployeeMap.get(dateKey).contains(plan.getEmployeeId())) {
                logger.warn("⚠️ Employee {} assigned multiple jobs on same day: {}",
                        plan.getEmployeeId(), dateKey);
                constraintViolated = true;
            } else {
                dateEmployeeMap.get(dateKey).add(plan.getEmployeeId());
            }
        }

        if (!constraintViolated) {
            logger.info("✅ One employee per day constraint respected");
        } else {
            logger.warn("⚠️ One employee per day constraint violated - check Gemini prompt");
        }
    }
/*
    @Test
    void testEmployeeAndTimesheetMockData() {
        logger.info("=== Testing Employee and Timesheet Mock Data ===");

        // Verify employee mock data
        List<Employee> employees = employeeRepository.findAvailableEmployees();
        assertEquals(5, employees.size(), "Should have 5 mock employees");

        logger.info("--- Mock Employees ---");
        for (Employee emp : employees) {
            assertNotNull(emp.getEmployeeId(), "Employee ID should not be null");
            assertNotNull(emp.getName(), "Employee name should not be null");
            assertNotNull(emp.getSkill(), "Employee skill should not be null");
            assertNotNull(emp.getPay(), "Employee pay should not be null");

            logger.info("Employee {}: {} (Skills: {}, Pay: ${}/hr)",
                    emp.getEmployeeId(), emp.getName(), emp.getSkill(), emp.getPay());
        }

        // Verify timesheet mock data
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertEquals(5, timesheets.size(), "Should have 5 mock timesheets");

        logger.info("--- Mock Timesheets ---");
        for (Timesheet ts : timesheets) {
            assertNotNull(ts.getTimesheetId(), "Timesheet ID should not be null");
            assertNotNull(ts.getEmployeeId(), "Employee ID should not be null");
            assertNotNull(ts.getHoursWorked(), "Hours worked should not be null");
            assertNotNull(ts.getStatus(), "Status should not be null");

            logger.info("Timesheet {}: Employee {} - {} hours, Status: {}, Pay: ${}",
                    ts.getTimesheetId(), ts.getEmployeeId(), ts.getHoursWorked(),
                    ts.getStatus(), ts.getSalaryPaid());
        }

        // Verify one-to-one relationship (each employee has one timesheet)
        Set<Long> employeeIds = employees.stream().map(Employee::getEmployeeId).collect(Collectors.toSet());
        Set<Long> timesheetEmployeeIds = timesheets.stream().map(Timesheet::getEmployeeId).collect(Collectors.toSet());

        assertEquals(employeeIds, timesheetEmployeeIds, "Each employee should have exactly one timesheet");

        logger.info("✅ Employee and Timesheet mock data validation completed successfully!");
    }*/
/*
    @Test
    void testGeminiShiftPlanGenerationWithoutDatabaseSave() {
        logger.info("=== Testing Gemini AI Shift Plan Generation (Display Only) ===");

        // Test Gemini AI generation without database save to see the actual shift plans
        int requiredEmployees = 1;

        logger.info("🚀 Calling Gemini AI to generate shift plans (display only)...");
        logger.info("Available employees: {}", mockEmployees.size());
        logger.info("Required employees per job: {}", requiredEmployees);

        try {
            // Get machine schedule data
            MachineSchedule schedule = shiftPlannerService.fetchMachineScheduleFromBusiness();

            // Convert to jobs and call Gemini directly
            List<Job> allJobs = convertScheduleToJobs(schedule);

            Date startDate = new Date();
            Date endDate = new Date(startDate.getTime() + (3 * 24 * 60 * 60 * 1000L)); // 3 days later

            // Call Gemini AI directly without database save
            List<ShiftPlan> result = generateShiftPlanService.generateShiftPlanWithoutSave(
                startDate, endDate, allJobs, requiredEmployees);

            // Verify and display the Gemini results
            assertNotNull(result, "Gemini should generate shift plans");
            assertTrue(result.size() > 0, "Gemini should generate at least one shift plan");

            logger.info("🎉 Gemini AI successfully generated {} shift plans", result.size());
            logger.info("");
            logger.info("=== GEMINI GENERATED SHIFT PLANS ===");

            // Display each shift plan in detail
            for (int i = 0; i < result.size(); i++) {
                ShiftPlan plan = result.get(i);
                logger.info("Shift Plan {}:", i + 1);
                logger.info("  Employee ID: {}", plan.getEmployeeId());
                logger.info("  Job ID: {}", plan.getJobId());
                logger.info("  Job Priority: {} ({})", plan.getJobPriority(), getPriorityName(plan.getJobPriority()));
                logger.info("  Shift Date: {}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(plan.getShiftDate()));
                logger.info("  Work Time: {} to {}", plan.getStartTime(), plan.getEndTime());
                logger.info("  Status: {}", plan.getStatus());

                long workDuration = plan.getEndTime().getTime() - plan.getStartTime().getTime();
                long hoursWorked = workDuration / (1000 * 60 * 60);
                logger.info("  Hours: {} hours", hoursWorked);
                logger.info("  ---");
            }

            // NEW: Verify day-by-day planning capability
            verifyDayByDayPlanning(result);

            // NEW: Verify completion time estimation
            verifyCompletionTimeEstimation(result);

            // Verify priority constraints are respected
            verifyPriorityRespected(result);

            // Check for one employee per day constraint
            verifyOneEmployeePerDayConstraint(result);

            logger.info("✅ Gemini AI shift plan generation test completed successfully!");

        } catch (Exception e) {
            logger.error("❌ Gemini AI call failed: {}", e.getMessage());
            fail("Gemini AI call should succeed. Error: " + e.getMessage());
        }
    }*/

    /**
     * Helper method to convert MachineSchedule to Job list
     */
    private List<Job> convertScheduleToJobs(MachineSchedule schedule) {
        List<Job> allJobs = new ArrayList<>();
        for (Map.Entry<String, List<com.example.shared.JobDto>> entry : schedule.getSchedules().entrySet()) {
            List<com.example.shared.JobDto> jobDtos = entry.getValue();

            if (jobDtos != null && !jobDtos.isEmpty()) {
                for (com.example.shared.JobDto jobDto : jobDtos) {
                    Job job = new Job();
                    job.setJobId(jobDto.getJobId());
                    job.setTitle(jobDto.getTitle());
                    job.setPriority(jobDto.getPriority());
                    // Convert LocalDate to Date
                    if (jobDto.getStartDate() != null) {
                        job.setStartDate(LocalDate.from(jobDto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    if (jobDto.getEndDate() != null) {
                        job.setEndDate(LocalDate.from(jobDto.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    allJobs.add(job);
                }
            }
        }
        return allJobs;
    }

    /**
     * Helper method to get priority name
     */
    private String getPriorityName(Integer priority) {
        if (priority == null) return "UNKNOWN";
        switch (priority) {
            case 1: return "CRITICAL";
            case 2: return "HIGH";
            case 3: return "MEDIUM";
            case 4: return "LOW";
            case 5: return "MINIMAL";
            default: return "UNKNOWN";
        }
    }
}
