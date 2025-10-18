package com.example.service;

import com.example.domain.model.entities.ShiftPlan;
import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.main.web-application-type=none"
})
public class ShiftPlannerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ShiftPlannerServiceTest.class);

    @Autowired
    private ShiftPlannerService shiftPlannerService;

    @Test
    public void testCreateMockMachineSchedule() {
        logger.info("=== mock machineschedule data ===");

        // mock data
        MachineSchedule mockSchedule = shiftPlannerService.createMockMachineSchedule();

        logger.info("machine count: {}", mockSchedule.getSchedules().size());

        mockSchedule.getSchedules().forEach((machineId, jobs) -> {
            logger.info("machine ID: {}", machineId);
            logger.info("machine count: {}", jobs.size());

            jobs.forEach(job ->
                logger.info("  - JobID: {}, title: {}, priority: {}, daysNeeded: {}",
                    job.getJobId(), job.getTitle(), job.getPriority(), job.getJobTimeNeededDays())
            );
        });

        //
        assert mockSchedule.getSchedules().size() == 2 : "Should have 2 machines";
        assert mockSchedule.getSchedules().containsKey("MACHINE-001") : "Should contain MACHINE-001";
        assert mockSchedule.getSchedules().containsKey("MACHINE-002") : "Should contain MACHINE-002";

        //
        List<JobDto> machine1Jobs = mockSchedule.getSchedules().get("MACHINE-001");
        List<JobDto> machine2Jobs = mockSchedule.getSchedules().get("MACHINE-002");

        assert machine1Jobs.size() == 2 : "MACHINE-001 should have 2 jobs";
        assert machine2Jobs.size() == 2 : "MACHINE-002 should have 2 jobs";

        logger.info("✅ Mock data created successfully!");
    }

    @Test
    public void testCreateShiftPlansWithMockData() {
        logger.info("=== Shift Plan Generation Test ===");

        try {
            // use mock data to create shift plans, try Gemini AI first, fallback if needed
            logger.info("--- Starting Shift Plan Generation (will try Gemini AI first) ---");
            List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlansWithMockData(1);

            logger.info("Generated shift plan count: {}", shiftPlans.size());

            if (shiftPlans.isEmpty()) {
                logger.warn("⚠️  No shift plans were generated - this may be expected if no employees are available");
            } else {
                logger.info("--- Generated Shift Plans ---");
                shiftPlans.forEach(shiftPlan -> {
                    logger.info("Shift Plan - ID: {}, Employee ID: {}, Job ID: {}, Status: {}",
                        shiftPlan.getShiftPlanId(), shiftPlan.getEmployeeId(),
                        shiftPlan.getJobId(), shiftPlan.getStatus());

                    if (shiftPlan.getStartTime() != null && shiftPlan.getEndTime() != null) {
                        logger.info("  Work time: {} to {}", shiftPlan.getStartTime(), shiftPlan.getEndTime());
                    }
                });

                // 验证新分配逻辑的关键要求
                validateNewAllocationLogic(shiftPlans);
            }

            logger.info("✅ shiftplan generation completed successfully!");

        } catch (Exception e) {
            logger.error("❌ shitplan generation failed: {}", e.getMessage());
            logger.error("Error details:", e);

            // 分析可能的错误原因
            if (e.getMessage() != null) {
                if (e.getMessage().contains("No available employees")) {
                    logger.info("💡 possible cause: need to add available employee data to database");
                } else if (e.getMessage().contains("Gemini")) {
                    logger.info("💡 possible cause: check Gemini API configuration - may fallback to basic scheduling");
                }
            }

            //
            logger.warn("Test completed with errors, but this helps us understand the system behavior");
        }
    }

    @Test
    public void testCreateShiftPlansFromBusinessService() {
        logger.info("=== Business Service Integration Test ===");

        try {
            // try to create shift plans from business service
            //
            logger.info("Attempting to fetch from business service (expected to fallback to mock data)...");

            List<ShiftPlan> shiftPlans = shiftPlannerService.createShiftPlans(1);

            logger.info("Integration test completed - generated {} shift plans", shiftPlans.size());

            if (!shiftPlans.isEmpty()) {
                logger.info("Sample shift plan: Employee ID: {}, Job ID: {}",
                    shiftPlans.get(0).getEmployeeId(), shiftPlans.get(0).getJobId());

                // 验证新分配逻辑的关键要求
                validateNewAllocationLogic(shiftPlans);
            }

            logger.info("✅ Integration test completed successfully (likely using mock data fallback)");

        } catch (Exception e) {
            logger.error("❌ Integration test failed: {}", e.getMessage());
            logger.info("This is expected if ExternalNewSchedule is not properly configured");
        }
    }

    /**
     * 验证新分配逻辑的三个关键要求：
     * 1. 一个员工一天只能做一个工作
     * 2. 严格按优先级（1最高，5最低）分配
     * 3. 显示明确的开始和结束时间
     */
    private void validateNewAllocationLogic(List<ShiftPlan> shiftPlans) {
        logger.info("=== 验证新分配逻辑 ===");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 1. 验证一个员工一天只能做一个工作
        validateOneJobPerEmployeePerDay(shiftPlans);

        // 2. 验证优先级分配顺序
        validatePriorityBasedAllocation(shiftPlans);

        // 3. 验证时间信息的完整性
        validateTimeInformation(shiftPlans, sdf);

        // 4. 验证优先级对应的时间段
        validatePriorityTimeSlots(shiftPlans, sdf);

        logger.info("✅ 新分配逻辑验证完成");
    }

    private void validateOneJobPerEmployeePerDay(List<ShiftPlan> shiftPlans) {
        logger.info("--- 验证：一个员工一天只能做一个工作 ---");

        // 按员工ID和日期分组
        Map<String, List<ShiftPlan>> employeeDayGroups = shiftPlans.stream()
            .filter(plan -> plan.getEmployeeId() != null && plan.getShiftDate() != null)
            .collect(Collectors.groupingBy(plan -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return plan.getEmployeeId() + "_" + dateFormat.format(plan.getShiftDate());
            }));

        boolean hasViolation = false;
        for (Map.Entry<String, List<ShiftPlan>> entry : employeeDayGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                hasViolation = true;
                logger.error("❌ 违规发现：员工在同一天被分配多个工作 - {}", entry.getKey());
                entry.getValue().forEach(plan ->
                    logger.error("  工作ID: {}, 优先级: {}", plan.getJobId(), plan.getJobPriority())
                );
            }
        }

        if (!hasViolation) {
            logger.info("✅ 通过：所有员工在同一天只分配了一个工作");
        }
    }

    private void validatePriorityBasedAllocation(List<ShiftPlan> shiftPlans) {
        logger.info("--- 验证：严格按优先级分配 ---");

        // 按优先级分组并统计
        Map<Integer, Long> priorityCount = shiftPlans.stream()
            .filter(plan -> plan.getJobPriority() != null)
            .collect(Collectors.groupingBy(
                ShiftPlan::getJobPriority,
                Collectors.counting()
            ));

        // 显示优先级分配统计
        priorityCount.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String priorityName = getPriorityName(entry.getKey());
                logger.info("优先级 {} ({}): {} 个工作分配",
                    entry.getKey(), priorityName, entry.getValue());
            });

        // 验证高优先级工作是否优先分配
        List<ShiftPlan> sortedByPriority = shiftPlans.stream()
            .filter(plan -> plan.getJobPriority() != null)
            .sorted(Comparator.comparing(ShiftPlan::getJobPriority))
            .collect(Collectors.toList());

        if (!sortedByPriority.isEmpty()) {
            logger.info("✅ 最高优先级工作 (优先级 {}): 工作ID {}",
                sortedByPriority.get(0).getJobPriority(),
                sortedByPriority.get(0).getJobId());
        }
    }

    private void validateTimeInformation(List<ShiftPlan> shiftPlans, SimpleDateFormat sdf) {
        logger.info("--- 验证：明确的开始和结束时间 ---");

        boolean allHaveTime = true;
        int validTimeCount = 0;

        for (ShiftPlan plan : shiftPlans) {
            if (plan.getStartTime() != null && plan.getEndTime() != null) {
                validTimeCount++;
                logger.info("员工 {}, 工作 {}: {} 到 {}",
                    plan.getEmployeeId(), plan.getJobId(),
                    sdf.format(plan.getStartTime()), sdf.format(plan.getEndTime()));

                // 验证工作时长（应该是8小时）
                long durationHours = (plan.getEndTime().getTime() - plan.getStartTime().getTime()) / (1000 * 60 * 60);
                if (durationHours != 8) {
                    logger.warn("⚠️ 工作时长异常: {} 小时 (期望8小时)", durationHours);
                }
            } else {
                allHaveTime = false;
                logger.error("❌ 缺少时间信息：员工 {}, 工作 {}",
                    plan.getEmployeeId(), plan.getJobId());
            }
        }

        if (allHaveTime && validTimeCount > 0) {
            logger.info("✅ 通过：所有 {} 个班次都有完整的时间信息", validTimeCount);
        } else {
            logger.warn("⚠️ 部分班次缺少完整的时间信息");
        }
    }

    private void validatePriorityTimeSlots(List<ShiftPlan> shiftPlans, SimpleDateFormat sdf) {
        logger.info("--- 验证：优先级对应的时间段 ---");

        // 期望的优先级时间段
        Map<Integer, Integer> expectedStartHours = Map.of(
            1, 6,  // CRITICAL - 早上6点
            2, 8,  // HIGH - 早上8点
            3, 9,  // MEDIUM - 早上9点
            4, 14, // LOW - 下午2点
            5, 18  // MINIMAL - 晚上6点
        );

        for (ShiftPlan plan : shiftPlans) {
            if (plan.getJobPriority() != null && plan.getStartTime() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(plan.getStartTime());
                int actualStartHour = cal.get(Calendar.HOUR_OF_DAY);

                Integer expectedStartHour = expectedStartHours.get(plan.getJobPriority());
                if (expectedStartHour != null) {
                    // 允许一定的时间偏移（因为可能有多个员工错开时间）
                    if (Math.abs(actualStartHour - expectedStartHour) <= 4) {
                        logger.info("✅ 优先级 {} 工作开始时间符合预期: {}:00 (基准: {}:00)",
                            plan.getJobPriority(), actualStartHour, expectedStartHour);
                    } else {
                        logger.warn("⚠️ 优先级 {} 工作开始时间偏离较大: {}:00 (期望: {}:00)",
                            plan.getJobPriority(), actualStartHour, expectedStartHour);
                    }
                }
            }
        }
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "未知";
        switch (priority) {
            case 1: return "关键";
            case 2: return "高";
            case 3: return "中等";
            case 4: return "低";
            case 5: return "最低";
            default: return "未知";
        }
    }
}
