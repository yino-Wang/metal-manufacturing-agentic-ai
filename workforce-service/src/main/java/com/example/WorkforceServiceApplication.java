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

        System.out.println("🗑️ Cleaning up existing data...");
        System.out.println("   - Found " + existingTimesheetCount + " existing timesheet records");
        System.out.println("   - Found " + existingEmployeeCount + " existing employee records");

        timesheetRepository.deleteAll();
        employeeRepository.deleteAll();

        System.out.println("✅ All previous timesheet and employee records deleted!");
        System.out.println("📋 Starting Workforce Management System with fresh data...");

        // Create three employees similar to business-service machines
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8080/api/workforce";

        // Employee 1 - Welder
        AddEmployeeRequest employee1 = new AddEmployeeRequest();
        employee1.setName("John Smith");
        employee1.setPay(28.0f);
        employee1.setSkill("Welding,Metal Cutting,Assembly");
        employee1.setPhoneNumber("555-0101");
        employee1.setSalary(4500.0f);
        employee1.setManagementArea("Production Floor A");
        employee1.setManagerName("Manager Anderson");
        employee1.setManager(false);

        // Employee 2 - Quality Inspector
        AddEmployeeRequest employee2 = new AddEmployeeRequest();
        employee2.setName("Sarah Johnson");
        employee2.setPay(32.0f);
        employee2.setSkill("Quality Control,Testing,Documentation");
        employee2.setPhoneNumber("555-0202");
        employee2.setSalary(5200.0f);
        employee2.setManagementArea("Quality Assurance");
        employee2.setManagerName("Supervisor Williams");
        employee2.setManager(false);

        // Employee 3 - Machine Operator
        AddEmployeeRequest employee3 = new AddEmployeeRequest();
        employee3.setName("Michael Brown");
        employee3.setPay(25.0f);
        employee3.setSkill("Machine Operation,Maintenance,Safety");
        employee3.setPhoneNumber("555-0303");
        employee3.setSalary(4200.0f);
        employee3.setManagementArea("Production Floor B");
        employee3.setManagerName("Manager Anderson");
        employee3.setManager(false);

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
        savedEmployee3 = employeeRepository.save(savedEmployee3);

        System.out.println("👷 Created Employee 1: " + savedEmployee1.getName() + " (ID: " + savedEmployee1.getEmployeeId() + ")");
        System.out.println("🔍 Created Employee 2: " + savedEmployee2.getName() + " (ID: " + savedEmployee2.getEmployeeId() + ")");
        System.out.println("⚙️ Created Employee 3: " + savedEmployee3.getName() + " (ID: " + savedEmployee3.getEmployeeId() + ")");

        // Generate one timesheet for each employee
        // Create timesheet for Employee 1 - John Smith (Welder)
        float hoursWorked1 = 8.5f;
        Date workDate1 = new Date(); // Today
        LocalDateTime clockInTime1 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0);
        LocalDateTime clockOutTime1 = clockInTime1.plusHours(8).plusMinutes(30);

        System.out.println("📊 Creating timesheet for " + savedEmployee1.getName() +
                " - " + hoursWorked1 + " hours (" + clockInTime1.getHour() + ":" +
                String.format("%02d", clockInTime1.getMinute()) + " - " +
                clockOutTime1.getHour() + ":" + String.format("%02d", clockOutTime1.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee1.getEmployeeId(),
                    workDate1,
                    hoursWorked1,
                    clockInTime1,
                    clockOutTime1
            );
            System.out.println("✅ Timesheet recorded successfully for " + savedEmployee1.getName());
        } catch (Exception e) {
            System.out.println("❌ Error recording timesheet for " + savedEmployee1.getName() + ": " + e.getMessage());
        }

        // Create timesheet for Employee 2 - Sarah Johnson (Quality Inspector)
        float hoursWorked2 = 7.0f;
        Date workDate2 = new Date(); // Today
        LocalDateTime clockInTime2 = LocalDateTime.now().withHour(8).withMinute(30).withSecond(0);
        LocalDateTime clockOutTime2 = clockInTime2.plusHours(7);

        System.out.println("📊 Creating timesheet for " + savedEmployee2.getName() +
                " - " + hoursWorked2 + " hours (" + clockInTime2.getHour() + ":" +
                String.format("%02d", clockInTime2.getMinute()) + " - " +
                clockOutTime2.getHour() + ":" + String.format("%02d", clockOutTime2.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee2.getEmployeeId(),
                    workDate2,
                    hoursWorked2,
                    clockInTime2,
                    clockOutTime2
            );
            System.out.println("✅ Timesheet recorded successfully for " + savedEmployee2.getName());
        } catch (Exception e) {
            System.out.println("❌ Error recording timesheet for " + savedEmployee2.getName() + ": " + e.getMessage());
        }

        // Create timesheet for Employee 3 - Michael Brown (Machine Operator)
        float hoursWorked3 = 9.0f;
        Date workDate3 = new Date(); // Today
        LocalDateTime clockInTime3 = LocalDateTime.now().withHour(7).withMinute(45).withSecond(0);
        LocalDateTime clockOutTime3 = clockInTime3.plusHours(9);

        System.out.println("📊 Creating timesheet for " + savedEmployee3.getName() +
                " - " + hoursWorked3 + " hours (" + clockInTime3.getHour() + ":" +
                String.format("%02d", clockInTime3.getMinute()) + " - " +
                clockOutTime3.getHour() + ":" + String.format("%02d", clockOutTime3.getMinute()) + ")");

        try {
            recordTimesheetService.recordTimesheet(
                    savedEmployee3.getEmployeeId(),
                    workDate3,
                    hoursWorked3,
                    clockInTime3,
                    clockOutTime3
            );
            System.out.println("✅ Timesheet recorded successfully for " + savedEmployee3.getName());
        } catch (Exception e) {
            System.out.println("❌ Error recording timesheet for " + savedEmployee3.getName() + ": " + e.getMessage());
        }

        System.out.println("🎉 Workforce Management System initialized successfully!");
        System.out.println("📈 Created 3 employees and 3 timesheets");
        System.out.println("💰 Total hours worked today: " + (hoursWorked1 + hoursWorked2 + hoursWorked3) + " hours");
        System.out.println("💵 Estimated daily payroll: $" +
                (hoursWorked1 * savedEmployee1.getPay() +
                        hoursWorked2 * savedEmployee2.getPay() +
                        hoursWorked3 * savedEmployee3.getPay()));
    }
}
