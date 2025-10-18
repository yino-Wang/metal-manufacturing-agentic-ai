package com.example.service;

import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * 临时的 ExternalNewSchedule 类，用于替代 business-service 依赖
 * 提供 mock 数据用于测试 Gemini AI 生成 shift plan 功能
 */
@Service
public class ExternalNewSchedule {

    /**
     * 获取 mock 机器调度数据
     * @return MachineSchedule 包含机器和任务的调度信息
     */
    public MachineSchedule fetchNewSchedule() {
        Map<String, List<JobDto>> scheduleMap = new HashMap<>();

        // 创建机器1的任务列表
        List<JobDto> machine1Jobs = Arrays.asList(
            createJobDto(1L, "High Priority Job", 1, 3, "Steel", 100),
            createJobDto(2L, "Low Priority Job", 5, 2, "Aluminum", 50)
        );

        // 创建机器2的任务列表
        List<JobDto> machine2Jobs = Arrays.asList(
            createJobDto(3L, "Urgent Job", 1, 2, "Iron", 75),
            createJobDto(4L, "Normal Job", 4, 1, "Copper", 25)
        );

        scheduleMap.put("MACHINE-001", machine1Jobs);
        scheduleMap.put("MACHINE-002", machine2Jobs);

        return new MachineSchedule(scheduleMap);
    }

    /**
     * 创建 JobDto 对象的辅助方法
     */
    private JobDto createJobDto(Long jobId, String title, Integer priority, Integer daysNeeded, String material, Integer amount) {
        JobDto jobDto = new JobDto();
        jobDto.setJobId(jobId);
        jobDto.setTitle(title);
        jobDto.setPriority(priority);
        jobDto.setJobTimeNeededDays(daysNeeded);
        jobDto.setDueDate(LocalDate.now().plusDays(7)); // 一周后到期
        jobDto.setStartDate(LocalDate.now().plusDays(1)); // 明天开始
        jobDto.setEndDate(LocalDate.now().plusDays(daysNeeded + 1)); // 根据所需天数计算结束日期
        jobDto.setMaterialNeeded(material);
        jobDto.setMaterialAmount(amount);
        return jobDto;
    }
}
