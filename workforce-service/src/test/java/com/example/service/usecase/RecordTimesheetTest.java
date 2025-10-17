package com.example.service.usecase;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.Timesheet;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.TimesheetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RecordTimesheetTest {
    @Autowired
    private RecordTimesheetService recordTimesheetService;
    @Autowired
    private TimesheetRepository timesheetRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data
        timesheetRepository.deleteAll();
        employeeRepository.deleteAll();

        // Create test employees - don't set employeeId manually, let it auto-generate
        Employee employee1 = new Employee();
        employee1.setName("John Doe");
        employee1.setPay(25.0f); // $25 per hour
        employee1.setSkill("Welding,Assembly");
        employee1 = employeeRepository.save(employee1); // Save first and get the generated ID

        Employee employee2 = new Employee();
        employee2.setName("Jane Smith");
        employee2.setPay(30.0f); // $30 per hour
        employee2.setSkill("Management,Quality Control");
        employee2 = employeeRepository.save(employee2); // Save first and get the generated ID

        // Create initial timesheet records using the actual saved employee IDs
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setEmployeeId(employee1.getEmployeeId());
        timesheet1.setWorkDate(new java.util.Date());
        timesheet1.setClockInTime(java.time.LocalDateTime.of(2025, 6, 1, 9, 0));
        timesheet1.setClockOutTime(java.time.LocalDateTime.of(2025, 6, 1, 17, 0));
        timesheet1.setHoursWorked(8f);
        timesheet1.setSalaryPaid(200.0f); // 8 hours * $25
        timesheet1.setStatus("NORMAL");
        timesheetRepository.save(timesheet1);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setEmployeeId(employee2.getEmployeeId());
        timesheet2.setWorkDate(new java.util.Date());
        timesheet2.setClockInTime(java.time.LocalDateTime.of(2025, 6, 1, 10, 0));
        timesheet2.setClockOutTime(java.time.LocalDateTime.of(2025, 6, 1, 16, 0));
        timesheet2.setHoursWorked(6f);
        timesheet2.setSalaryPaid(180.0f); // 6 hours * $30
        timesheet2.setStatus("NORMAL");
        timesheetRepository.save(timesheet2);
    }

    @Test
    void testRecordTimesheet() {
        // Get initial count
        long initialCount = timesheetRepository.count();

        // Test recording timesheet for employee 1
        recordTimesheetService.recordTimesheet(1L, new java.util.Date(), 8f,
                java.time.LocalDateTime.of(2025, 6, 2, 9, 0),
                java.time.LocalDateTime.of(2025, 6, 2, 17, 0));

        // Test recording timesheet for employee 2
        recordTimesheetService.recordTimesheet(2L, new java.util.Date(), 6f,
                java.time.LocalDateTime.of(2025, 6, 2, 10, 0),
                java.time.LocalDateTime.of(2025, 6, 2, 16, 0));

        // Verify that new timesheet records were created
        long finalCount = timesheetRepository.count();
        assertEquals(initialCount + 2, finalCount, "Two new timesheet records should be created");

        // Verify the details of the created timesheets
        List<Timesheet> allTimesheets = timesheetRepository.findAll();

        // Find the newly created timesheets (should be the last two)
        Timesheet newTimesheet1 = allTimesheets.stream()
                .filter(ts -> ts.getEmployeeId().equals(1L) &&
                        ts.getClockInTime().equals(java.time.LocalDateTime.of(2025, 6, 2, 9, 0)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("New timesheet for employee 1 not found"));

        Timesheet newTimesheet2 = allTimesheets.stream()
                .filter(ts -> ts.getEmployeeId().equals(2L) &&
                        ts.getClockInTime().equals(java.time.LocalDateTime.of(2025, 6, 2, 10, 0)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("New timesheet for employee 2 not found"));

        // Verify employee 1's timesheet
        assertNotNull(newTimesheet1, "Employee 1's timesheet should exist");
        assertEquals(1L, newTimesheet1.getEmployeeId(), "Employee ID should match");
        assertEquals(8f, newTimesheet1.getHoursWorked(), "Hours worked should match");
        assertEquals(200.0f, newTimesheet1.getSalaryPaid(), 0.01f, "Salary should be calculated correctly (8 * $25)");
        assertEquals("EXCEPTION", newTimesheet1.getStatus(), "Status should be EXCEPTION when no shift plan exists");

        // Verify employee 2's timesheet
        assertNotNull(newTimesheet2, "Employee 2's timesheet should exist");
        assertEquals(2L, newTimesheet2.getEmployeeId(), "Employee ID should match");
        assertEquals(6f, newTimesheet2.getHoursWorked(), "Hours worked should match");
        assertEquals(180.0f, newTimesheet2.getSalaryPaid(), 0.01f, "Salary should be calculated correctly (6 * $30)");
        assertEquals("EXCEPTION", newTimesheet2.getStatus(), "Status should be EXCEPTION when no shift plan exists");

        System.out.println("✅ All timesheet recording tests passed successfully!");
        System.out.println("📊 Employee 1: 8 hours worked, $200 salary calculated");
        System.out.println("📊 Employee 2: 6 hours worked, $180 salary calculated");
    }

    @Test
    void testRecordTimesheetWithInvalidEmployee() {
        // Test recording timesheet for non-existent employee
        assertThrows(RuntimeException.class, () -> {
            recordTimesheetService.recordTimesheet(999L, new java.util.Date(), 8f,
                    java.time.LocalDateTime.of(2025, 6, 2, 9, 0),
                    java.time.LocalDateTime.of(2025, 6, 2, 17, 0));
        }, "Should throw RuntimeException for non-existent employee");

        System.out.println("✅ Invalid employee test passed - proper exception handling!");
    }
}
