package com.example.config;

import com.example.domain.model.aggregates.Employee;
import com.example.infrastructure.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;

@Configuration
@Profile({"test", "dev"}) //
public class MockDataConfig {

    @Bean
    public CommandLineRunner initMockEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            // 检查是否已有员工数据，避免重复创建
            if (employeeRepository.count() == 0) {
                createMockEmployees(employeeRepository);
            }
        };
    }

    private void createMockEmployees(EmployeeRepository employeeRepository) {
        System.out.println("=== 初始化模拟员工数据 ===");

        // 创建5个模拟员工
        Employee employee1 = new Employee(
            null,                    // employeeId (自动生成)
            "Lee",                   // name
            "138-0000-0001",        // phoneNumber
            5000.0f,                // salary
            25.0f,                  // pay
            "welding,machining",    // skill
            new Date(),             // startDatePayslip
            new Date(),             // endDatePayslip
            null,                   // scheduleId
            false,                  // manager
            "Manager1",                // managerName
            null,                   // managementArea
            2                       // scheduledJobs (max number of jobs assigned)
        );

        Employee employee2 = new Employee(
            null,
            "Mandy",
            "138-0000-0002",
            4500.0f,
            22.0f,
            "assembly,quality_control",
            new Date(),
            new Date(),
            null,
            false,
            "Manager1",
            null,
            1
        );

        Employee employee3 = new Employee(
            null,
            "John",
            "138-0000-0003",
            6000.0f,
            30.0f,
            "machining,programming",
            new Date(),
            new Date(),
            null,
            false,
            "Manager1",
            null,
            2
        );




        // save
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);


        System.out.println("✅ create 3 mock employees completed.");

    }
}
