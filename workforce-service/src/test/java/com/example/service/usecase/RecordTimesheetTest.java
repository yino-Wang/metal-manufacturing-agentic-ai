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

    private Long employeeId1;
    private Long employeeId2;

    @BeforeEach
    void setUp() {
        // Clear existing data
        timesheetRepository.deleteAll();
        employeeRepository.deleteAll();

        // Create test employees - let Hibernate auto-generate IDs
        Employee employee1 = new Employee();
        // Don't set employeeId manually - let it auto-generate
        employee1.setName("John Doe");
        employee1.setPay(25.0f); // $25 per hour
        employee1.setSkill("Welding,Assembly");
        employee1 = employeeRepository.save(employee1); // Save and get the generated ID

        Employee employee2 = new Employee();
        // Don't set employeeId manually - let it auto-generate
        employee2.setName("Jane Smith");
        employee2.setPay(30.0f); // $30 per hour
        employee2.setSkill("Management,Quality Control");
        employee2 = employeeRepository.save(employee2); // Save and get the generated ID

        // Store the actual generated IDs for use in tests
        employeeId1 = employee1.getEmployeeId();
        employeeId2 = employee2.getEmployeeId();

        System.out.println("Created Employee 1 with ID: " + employeeId1);
        System.out.println("Created Employee 2 with ID: " + employeeId2);
        //employee1 info
        System.out.println("Employe1 Name: " + employee1.getName());
        System.out.println("Employee1 Pay Rate: $" + employee1.getPay() + " per hour");
        System.out.println("Employee1 Skills: " + employee1.getSkill());
        //employee2 info
        System.out.println("Employe2 Name: " + employee2.getName());
        System.out.println("Employee2 Pay Rate: $" + employee2.getPay() + " per hour");
        System.out.println("Employee2 Skills: " + employee2.getSkill());
    }

    @Test
    void testRecordTimesheet() {
        // Get initial count
        long initialCount = timesheetRepository.count();

        // Test recording timesheet for employee 1
        recordTimesheetService.recordTimesheet(employeeId1, new java.util.Date(), 8f,
                java.time.LocalDateTime.of(2025, 6, 2, 9, 0),
                java.time.LocalDateTime.of(2025, 6, 2, 17, 0));

        // Test recording timesheet for employee 2
        recordTimesheetService.recordTimesheet(employeeId2, new java.util.Date(), 6f,
                java.time.LocalDateTime.of(2025, 6, 2, 10, 0),
                java.time.LocalDateTime.of(2025, 6, 2, 16, 0));

        // Verify that new timesheet records were created
        long finalCount = timesheetRepository.count();
        assertEquals(initialCount + 2, finalCount, "Two new timesheet records should be created");

        // Verify the details of the created timesheets
        List<Timesheet> allTimesheets = timesheetRepository.findAll();

        // Find the newly created timesheets (should be the last two)
        Timesheet newTimesheet1 = allTimesheets.stream()
                .filter(ts -> ts.getEmployeeId().equals(employeeId1) &&
                        ts.getClockInTime().equals(java.time.LocalDateTime.of(2025, 6, 2, 9, 0)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("New timesheet for employee 1 not found"));

        Timesheet newTimesheet2 = allTimesheets.stream()
                .filter(ts -> ts.getEmployeeId().equals(employeeId2) &&
                        ts.getClockInTime().equals(java.time.LocalDateTime.of(2025, 6, 2, 10, 0)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("New timesheet for employee 2 not found"));

        // Verify employee 1's timesheet
        assertNotNull(newTimesheet1, "Employee 1's timesheet should exist");
        assertEquals(employeeId1, newTimesheet1.getEmployeeId(), "Employee ID should match");
        assertEquals(8f, newTimesheet1.getHoursWorked(), "Hours worked should match");
        assertEquals(200.0f, newTimesheet1.getSalaryPaid(), 0.01f, "Salary should be calculated correctly (8 * $25)");
        assertEquals("EXCEPTION", newTimesheet1.getStatus(), "Status should be EXCEPTION when no shift plan exists");

        // Verify employee 2's timesheet
        assertNotNull(newTimesheet2, "Employee 2's timesheet should exist");
        assertEquals(employeeId2, newTimesheet2.getEmployeeId(), "Employee ID should match");
        assertEquals(6f, newTimesheet2.getHoursWorked(), "Hours worked should match");
        assertEquals(180.0f, newTimesheet2.getSalaryPaid(), 0.01f, "Salary should be calculated correctly (6 * $30)");
        assertEquals("EXCEPTION", newTimesheet2.getStatus(), "Status should be EXCEPTION when no shift plan exists");

        System.out.println("✅ All timesheet recording tests passed successfully!");

        System.out.println("📊 Employee 1: 8 hours worked, $200 salary calculated");
        System.out.println("📊 Employee 2: 6 hours worked, $180 salary calculated");
        System.out.println("Employee 1 Timesheet Status: " + newTimesheet1.getStatus() +
                "Employee 1 Timesheet id: " + newTimesheet1.getTimesheetId() + ", " + newTimesheet1.getEmployeeId() + ", " +newTimesheet1.getWorkDate() + ", " + newTimesheet1.getClockInTime() + ", " + newTimesheet1.getClockOutTime() + ", " + newTimesheet1.getHoursWorked() + ", " + newTimesheet1.getSalaryPaid() + ", " + newTimesheet1.getStatus());
        System.out.println("Employee 2 Timesheet Status: " + newTimesheet2.getStatus()
                + "Employee 2 Timesheet id: " + newTimesheet2.getTimesheetId() + ", " + newTimesheet2.getEmployeeId() + ", " +newTimesheet2.getWorkDate() + ", " + newTimesheet2.getClockInTime() + ", " + newTimesheet2.getClockOutTime() + ", " + newTimesheet2.getHoursWorked() + ", " + newTimesheet2.getSalaryPaid() + ", " + newTimesheet2.getStatus());
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
