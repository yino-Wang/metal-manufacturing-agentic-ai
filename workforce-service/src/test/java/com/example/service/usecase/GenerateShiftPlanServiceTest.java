package com.example.service.usecase;

import com.example.service.DTO.AutoScheduleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.service.usecase.GenerateShiftPlanService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GenerateShiftPlanServiceTest {
    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;

    @Test
    void testAutoGenerateShiftPlan() {
        // test data
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000); // +1天
        Integer jobId = 1;
        int requiredEmployees = 2;
        String shiftType = "morning";

        AutoScheduleResponse response = generateShiftPlanService.autoGenerateShiftPlan(
                startDate, endDate, jobId, requiredEmployees, shiftType
        );
        assertNotNull(response.getShiftSchedules(), "排班结果不能为空");
        assertTrue(response.getShiftSchedules().size() > 0, "排班列表应有数据");
    }
}
