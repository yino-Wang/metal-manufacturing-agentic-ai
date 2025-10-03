package com.example.infrastructure.messaging;
import com.example.domain.model.ShiftSchedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// Service for publishing shift schedule events to Kafka,
// enabling other services to react to schedule changes.

@Service
public class KafakaScheduleProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(KafakaScheduleProducer.class);

    //publish shift schedule to topic
    public void publishShiftSchedule(ShiftSchedule shiftSchedule, String eventType) throws JsonProcessingException {
        try {
            // build message body (can include event type and data)
            Map<String, Object> payload = new HashMap<>();
            payload.put("eventType", eventType); // 如 "CREATED", "UPDATED"
            payload.put("data", shiftSchedule);
            String message = new ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send("shift-schedule-topic", message);
            logger.info("Published shift schedule event: {}", message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize shift schedule", e);
        }
    }

    private String serializeSchedule(ShiftSchedule schedule) throws JsonProcessingException {
        //
        return new ObjectMapper().writeValueAsString(schedule);
    }
}
