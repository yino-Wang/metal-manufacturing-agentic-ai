package com.example.service.usecase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class KafkaTopicStatusTest {

    @Autowired
    private StreamBridge streamBridge;

    @Test
    void checkKafkaTopicsWorking() {
        System.out.println("=".repeat(60));
        System.out.println("🔍 KAFKA TOPIC Runner - Workforce Service");
        System.out.println("=".repeat(60));

        try {
            // test: send info to timesheet-events topic
            boolean timesheetResult = streamBridge.send("timesheetEvent-out-0", "Test message for timesheet-events");
            System.out.println("📊 timesheet-events Topic: " + (timesheetResult ? "✅ is working" : "❌ failed to send message"));

            // 测试发送消息到 shift-published-events topic
            boolean shiftResult = streamBridge.send("shiftPublished-out-0", "Test message for shift-published-events");
            System.out.println("📅 shift-published-events Topic: " + (shiftResult ? "✅ is working" : "❌ failed to send message"));

            System.out.println("\n🔗 Topic config info:");
            System.out.println("  • timesheet-events - send timesheet relevant events");
            System.out.println("  • machine-schedule-events - receive business-service machine schedule events");
            System.out.println("  • shift-published-events - send shift published events ");

            System.out.println("\n📡 Kafka connection info:");
            System.out.println("  • Broker: localhost:9092");
            System.out.println("  • Consumer Group: workforce-service-group");

        } catch (Exception e) {
            System.out.println("❌ Kafka connection failed: " + e.getMessage());
        }

        System.out.println("=".repeat(60));
    }
}
