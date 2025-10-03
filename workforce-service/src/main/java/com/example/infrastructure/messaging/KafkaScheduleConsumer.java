package com.example.infrastructure.messaging;
import com.example.domain.model.ShiftSchedule;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.service.usecase.GenerateShiftPlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.logging.Logger;

@Service
public class KafkaScheduleConsumer {
    @Autowired
    private GenerateShiftPlanService generateShiftPlanService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private org.slf4j.Logger logger;

    // 1. Listen to Business Management's ScheduleCreated event
    //just draft, will adjust based on the business management event structure

    @KafkaListener(topics = "schedule-created-topic", groupId = "workforce-group")
    public void consumeScheduleCreated(String message) throws JsonProcessingException {
        // Parse event (assume JSON with jobId, startDate, endDate, requiredEmployees, shiftType)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(message);
        Integer jobId = node.get("jobId").asInt();
        String shiftType = node.get("shiftType").asText();
        int requiredEmployees = node.get("requiredEmployees").asInt();
        Date startDate = new Date(node.get("startDate").asLong());
        Date endDate = new Date(node.get("endDate").asLong());
        // Trigger agentic shift plan generation
        generateShiftPlanService.generateShiftPlan(startDate, endDate, jobId, requiredEmployees, shiftType);
    }

    // 2. Listen to local shift schedule events (for notification, etc.)
    @KafkaListener(topics = "shift-schedule-topic", groupId = "workforce-group")
    public void consumeScheduleEvent(String message) throws JsonProcessingException {
        ShiftSchedule schedule = deserializeSchedule(message);
        handleScheduleEvent(schedule);
    }

    private ShiftSchedule deserializeSchedule(String message) throws JsonProcessingException {
        return new ObjectMapper().readValue(message, ShiftSchedule.class);
    }

    private void handleScheduleEvent(ShiftSchedule schedule) {
        // 1. Notify employee (call service method)
        generateShiftPlanService.notifyEmployee(schedule);
        // 2. Log the event for audit
        logger.info("Shift schedule published for employee {} on {}", schedule.getEmployeeId(), schedule.getShiftDate());

        // 3. Optionally call other microservices （todo）

    }
}
