package com.example;

import com.example.controller.dto.AddEmployeeRequest;
import com.example.controller.dto.RecordTimesheetRequest;
import com.example.domain.model.aggregates.Employee;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.TimesheetRepository;
import com.example.service.usecase.RecordTimesheetService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
public class WorkforceServiceApplication {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(WorkforceServiceApplication.class, args);

        // Get required services from Spring context
        EmployeeRepository employeeRepository = context.getBean(EmployeeRepository.class);
        RecordTimesheetService recordTimesheetService = context.getBean(RecordTimesheetService.class);
        TimesheetRepository timesheetRepository = context.getBean(TimesheetRepository.class);

        // Clear existing data (clean slate for fresh start)
        long existingTimesheetCount = timesheetRepository.count();
        long existingEmployeeCount = employeeRepository.count();

        System.out.println("Cleaning up existing data...");
        System.out.println("   - Found " + existingTimesheetCount + " existing timesheet records");
        System.out.println("   - Found " + existingEmployeeCount + " existing employee records");

        timesheetRepository.deleteAll();
        employeeRepository.deleteAll();

        System.out.println("All previous timesheet and employee records deleted!");
        System.out.println("Starting Workforce Management System with fresh data...");

        // Create three employees similar to business-service machines
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8080/api/workforce";

        // Employee 1 - Welder
        AddEmployeeRequest employee1 = new AddEmployeeRequest();
        employee1.setName("John Smith");
        employee1.setPay(28.0f);
        employee1.setSkill("Normal");
        employee1.setPhoneNumber("555-0101");
        employee1.setSalary(4500.0f);
        employee1.setManagementArea("Production Floor A");
        employee1.setManagerName("None");
        employee1.setManager(false);
        employee1.setStatus("AVAILABLE");

        // Employee 2 - Quality Inspector
        AddEmployeeRequest employee2 = new AddEmployeeRequest();
        employee2.setName("Sarah Johnson");
        employee2.setPay(32.0f);
        employee2.setSkill("Normal");
        employee2.setPhoneNumber("555-0202");
        employee2.setSalary(5200.0f);
        employee2.setManagementArea("None");
        employee2.setManagerName("Supervisor Williams");
        employee2.setManager(false);
        employee2.setStatus("AVAILABLE");

        // Employee 3 - Machine Operator
        AddEmployeeRequest employee3 = new AddEmployeeRequest();
        employee3.setName("Michael Brown");
        employee3.setPay(25.0f);
        employee3.setSkill("Normal");
        employee3.setPhoneNumber("555-0303");
        employee3.setSalary(4200.0f);
        employee3.setManagementArea("None");
        employee3.setManagerName("Manager Anderson");
        employee3.setManager(false);
        employee3.setStatus("AVAILABLE");

        // Employee 4
        AddEmployeeRequest employee4 = new AddEmployeeRequest();
        employee4.setName("Carl Davis");
        employee4.setPay(29.0f);
        employee4.setSkill("Normal");
        employee4.setPhoneNumber("555-0404");
        employee4.setSalary(3200.0f);
        employee4.setManagementArea("None");
        employee4.setManagerName("Manager Anderson");
        employee4.setManager(false);
        employee4.setStatus("AVAILABLE");

        // Save employees directly to database (simpler approach)
        Employee savedEmployee1 = new Employee();
        savedEmployee1.setName(employee1.getName());
        savedEmployee1.setPay(employee1.getPay());
        savedEmployee1.setSkill(employee1.getSkill());
        savedEmployee1.setPhoneNumber(employee1.getPhoneNumber());
        savedEmployee1.setSalary(employee1.getSalary());
        savedEmployee1.setManagementArea(employee1.getManagementArea());
        savedEmployee1.setManagerName(employee1.getManagerName());
        savedEmployee1.setManager(employee1.getManager());
        savedEmployee1.setStatus(employee1.getStatus());
        savedEmployee1 = employeeRepository.save(savedEmployee1);

        Employee savedEmployee2 = new Employee();
        savedEmployee2.setName(employee2.getName());
        savedEmployee2.setPay(employee2.getPay());
        savedEmployee2.setSkill(employee2.getSkill());
        savedEmployee2.setPhoneNumber(employee2.getPhoneNumber());
        savedEmployee2.setSalary(employee2.getSalary());
        savedEmployee2.setManagementArea(employee2.getManagementArea());
        savedEmployee2.setManagerName(employee2.getManagerName());
        savedEmployee2.setManager(employee2.getManager());
        savedEmployee2.setStatus(employee2.getStatus());
        savedEmployee2 = employeeRepository.save(savedEmployee2);

        Employee savedEmployee3 = new Employee();
        savedEmployee3.setName(employee3.getName());
        savedEmployee3.setPay(employee3.getPay());
        savedEmployee3.setSkill(employee3.getSkill());
        savedEmployee3.setPhoneNumber(employee3.getPhoneNumber());
        savedEmployee3.setSalary(employee3.getSalary());
        savedEmployee3.setManagementArea(employee3.getManagementArea());
        savedEmployee3.setManagerName(employee3.getManagerName());
        savedEmployee3.setManager(employee3.getManager());
        savedEmployee3.setStatus(employee3.getStatus());
        savedEmployee3 = employeeRepository.save(savedEmployee3);

        Employee savedEmployee4 = new Employee();
        savedEmployee4.setName(employee4.getName());
        savedEmployee4.setPay(employee4.getPay());
        savedEmployee4.setSkill(employee4.getSkill());
        savedEmployee4.setPhoneNumber(employee4.getPhoneNumber());
        savedEmployee4.setSalary(employee4.getSalary());
        savedEmployee4.setManagementArea(employee4.getManagementArea());
        savedEmployee4.setManagerName(employee4.getManagerName());
        savedEmployee4.setManager(employee4.getManager());
        savedEmployee4.setStatus(employee4.getStatus());
        savedEmployee4 = employeeRepository.save(savedEmployee4);

        System.out.println("Created Employee 1: " + savedEmployee1.getName() + " (ID: " + savedEmployee1.getEmployeeId() + ")");
        System.out.println("Created Employee 2: " + savedEmployee2.getName() + " (ID: " + savedEmployee2.getEmployeeId() + ")");
        System.out.println("⚙Created Employee 3: " + savedEmployee3.getName() + " (ID: " + savedEmployee3.getEmployeeId() + ")");
        System.out.println("⚙Created Employee 4: " + savedEmployee4.getName() + " (ID: " + savedEmployee4.getEmployeeId() + ")");

        // Generate one timesheet for each employee
        // Create timesheet for Employee 1 - John Smith (Welder)
        float hoursWorked1 = 7.5f;
        Long jobId1 = 1L;
        Date workDate1 = new Date(); // Today
        LocalDateTime clockInTime1 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime clockOutTime1 = clockInTime1.plusHours(7).plusMinutes(30);

        System.out.println("Creating timesheet for " + savedEmployee1.getName() +
                " - " + hoursWorked1 + " hours (" + clockInTime1.getHour() + ":" +
                String.format("%02d", clockInTime1.getMinute()) + " - " +
                clockOutTime1.getHour() + ":" + String.format("%02d", clockOutTime1.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee1.getEmployeeId(),
                    jobId1,
                    workDate1,
                    hoursWorked1,
                    clockInTime1,
                    clockOutTime1
            );
            System.out.println("Timesheet recorded successfully for " + savedEmployee1.getName());
        } catch (Exception e) {
            System.out.println("Error recording timesheet for " + savedEmployee1.getName() + ": " + e.getMessage());
        }

        // Create timesheet for Employee 2 - Sarah Johnson (Quality Inspector)
        float hoursWorked2 = 7.0f;
        Long jobId2 = 2L;
        Date workDate2 = new Date(); // Today
        LocalDateTime clockInTime2 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime clockOutTime2 = clockInTime2.plusHours(7);

        System.out.println("Creating timesheet for " + savedEmployee2.getName() +
                " - " + hoursWorked2 + " hours (" + clockInTime2.getHour() + ":" +
                String.format("%02d", clockInTime2.getMinute()) + " - " +
                clockOutTime2.getHour() + ":" + String.format("%02d", clockOutTime2.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee2.getEmployeeId(),
                    jobId2,
                    workDate2,
                    hoursWorked2,
                    clockInTime2,
                    clockOutTime2
            );
            System.out.println("Timesheet recorded successfully for " + savedEmployee2.getName());
        } catch (Exception e) {
            System.out.println("Error recording timesheet for " + savedEmployee2.getName() + ": " + e.getMessage());
        }

        // Create timesheet for Employee 3 - Michael Brown (Machine Operator)
        float hoursWorked3 = 6.0f;
        Long jobId3 = 3L;
        Date workDate3 = new Date(); // Today
        LocalDateTime clockInTime3 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime clockOutTime3 = clockInTime3.plusHours(6);

        System.out.println("Creating timesheet for " + savedEmployee3.getName() +
                " - " + hoursWorked3 + " hours (" + clockInTime3.getHour() + ":" +
                String.format("%02d", clockInTime3.getMinute()) + " - " +
                clockOutTime3.getHour() + ":" + String.format("%02d", clockOutTime3.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee3.getEmployeeId(),
                    jobId3,
                    workDate3,
                    hoursWorked3,
                    clockInTime3,
                    clockOutTime3
            );
            System.out.println("Timesheet recorded successfully for " + savedEmployee3.getName());
        } catch (Exception e) {
            System.out.println("Error recording timesheet for " + savedEmployee3.getName() + ": " + e.getMessage());
        }

        // Create timesheet for Employee 4 - Carl Davis (Machine Operator)
        float hoursWorked4 = 6.0f;
        Long jobId4 = 4L;
        Date workDate4 = new Date(); // Today
        LocalDateTime clockInTime4 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0);
        LocalDateTime clockOutTime4 = clockInTime4.plusHours(6);
        System.out.println("Creating timesheet for " + savedEmployee4.getName() +
                " - " + hoursWorked4 + " hours (" + clockInTime4.getHour() + ":" +
                String.format("%02d", clockInTime4.getMinute()) + " - " +
                clockOutTime4.getHour() + ":" + String.format("%02d", clockOutTime4.getMinute()) + ")");
        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee4.getEmployeeId(),
                    jobId4,
                    workDate4,
                    hoursWorked4,
                    clockInTime4,
                    clockOutTime4
            );
            System.out.println("Timesheet recorded successfully for " + savedEmployee4.getName());
        } catch (Exception e) {
            System.out.println("Error recording timesheet for " + savedEmployee4.getName() + ": " + e.getMessage());
        }

        System.out.println("Workforce Management System initialized successfully!");
        System.out.println("Created 4 employees and 4 timesheets");
        System.out.println("Total hours worked today: " + (hoursWorked1 + hoursWorked2 + hoursWorked3 + hoursWorked4) + " hours");
        System.out.println("Estimated daily payroll: $" +
                (hoursWorked1 * savedEmployee1.getPay() +
                        hoursWorked2 * savedEmployee2.getPay() +
                        hoursWorked3 * savedEmployee3.getPay() +
                        hoursWorked4 * savedEmployee4.getPay()));

    }
}
