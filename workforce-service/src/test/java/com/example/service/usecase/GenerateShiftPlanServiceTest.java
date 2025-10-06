package com.example.service.usecase;

import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.commands.GenerateShiftPlanCommand;
import com.example.domain.model.entities.IndividualSchedule;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.infrastructure.client.OpenAIClient;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.IndividualScheduleRepository;
import com.example.infrastructure.repository.JobRepository;
import com.example.service.DTO.AutoScheduleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // 导入 Mockito
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration; // 导入新的 TestConfiguration
import org.springframework.context.annotation.Bean; // 导入 Bean 注解

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GenerateShiftPlanServiceTest {

    // use TestConfiguration to define Mock Bean
    @TestConfiguration
    static class TestConfig {
        // use  Mockito to create a mock OpenAIClient
        @Bean
        OpenAIClient openAIClient() {
            return Mockito.mock(OpenAIClient.class);
        }
    }

    // 注入 Service 和 Repositories
    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private IndividualScheduleRepository individualScheduleRepository;

    // 注意：OpenAIClient 现在已经被 TestConfig 内部的 @Bean 方法 Mock 掉了，
    // 所以这里不需要再声明 @MockBean 或 @Autowired

    // 存储动态创建的实体 ID
    private Long testJobId;
    private Long testEmployeeId;

    @BeforeEach
    void setup() {
        // 1. 创建并保存 Job
        Job job = new Job();
        job.setTitle("Test Job Title");
        jobRepository.save(job);
        this.testJobId = job.getJobId();

        // 2. 创建并保存 Employee
        Employee employee = new Employee();
        employee.setName("Test Employee");
        employee.setPay(100f);
        employee.setSkill("morning");
        employeeRepository.save(employee);
        this.testEmployeeId = employee.getEmployeeId();

        // 3. 为员工创建个人排班
        IndividualSchedule schedule = new IndividualSchedule();
        schedule.setAssignedEmployee(employee);
        schedule.setFinishTime(new Date());
        schedule.setNextJob("Test Job");
        individualScheduleRepository.save(schedule);
    }

    @Test
    void testAutoGenerateShiftPlan() {
        // 测试数据：使用动态捕获的 ID
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000); // +1天
        int requiredEmployees = 2;
        String shiftType = "morning";

        // 调用服务，使用动态 ID
        AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, this.testJobId, requiredEmployees, shiftType
        );

        assertNotNull(response.getShiftSchedules(), "schedule should not be null");
        assertTrue(!response.getShiftSchedules().isEmpty(), "schedule should not be empty");
    }

    @Test
    void testGenerateShiftPlanWithCommand() {
        // 测试数据：使用动态捕获的 ID
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000); // +1天
        int requiredEmployees = 2;
        String shiftType = "morning";

        // 创建 Command，使用动态 ID
        GenerateShiftPlanCommand command = new GenerateShiftPlanCommand(
                this.testJobId,
                this.testEmployeeId,
                startDate,
                endDate,
                requiredEmployees,
                shiftType
        );

        List<ShiftSchedule> result = generateShiftPlanService.generateShiftPlan(command);

        assertNotNull(result, "schedule should not be null");
        assertTrue(!result.isEmpty(), "schedule should not be empty");
    }
}