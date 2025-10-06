package com.example.infrastructure.client;

import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.aggregates.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OpenAIClient implements LLMClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    private final SimpleDateFormat dateFormat;

    public OpenAIClient(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
        this.objectMapper = new ObjectMapper();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public List<ShiftSchedule> generateShiftPlan(AgentInput input) {
        try {
            String prompt = buildPrompt(input);
            logger.info("Sending prompt to OpenAI: {}", prompt);

            var completion = openAiService.createChatCompletion(
                    ChatCompletionRequest.builder()
                            .model("gpt-4")
                            .messages(Arrays.asList(
                                    new ChatMessage("system", "You are an AI scheduling assistant specialized in creating optimal shift plans. " +
                                            "Return the schedule in JSON array format with the following structure: " +
                                            "[{\"employeeId\": number, \"shiftDate\": \"YYYY-MM-DD\", \"shiftType\": string}]") ,
                                    new ChatMessage("user", prompt)
                            ))
                            .temperature(0.7)
                            .build()
            );

            String response = completion.getChoices().get(0).getMessage().getContent();
            logger.info("Received response from OpenAI: {}", response);

            return parseResponse(response, input);
        } catch (Exception e) {
            logger.error("Error generating shift plan: ", e);
            throw new RuntimeException("Failed to generate shift plan", e);
        }
    }

    private String buildPrompt(AgentInput input) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate an optimal shift schedule with the following requirements:\n\n");

        // Time period
        prompt.append("Time period:\n");
        prompt.append("- Start: ").append(dateFormat.format(input.getStartTime())).append("\n");
        prompt.append("- End: ").append(dateFormat.format(input.getEndTime())).append("\n\n");

        // Available employees
        prompt.append("Available employees:\n");
        for (Employee employee : input.getAvailableEmployees()) {
            prompt.append("- ID: ").append(employee.getEmployeeId())
                    .append(", Name: ").append(employee.getName())
                    .append(", Skills: ").append(employee.getSkill())
                    .append("\n");
        }
        prompt.append("\n");

        // Staffing requirements
        prompt.append("Staffing requirements:\n");
        for (Map.Entry<String, Integer> entry : input.getStaffingRequirements().entrySet()) {
            prompt.append("- Shift type: ").append(entry.getKey())
                    .append(", Required employees: ").append(entry.getValue())
                    .append("\n");
        }
        prompt.append("\n");

        // Constraints
        prompt.append("Constraints:\n");
        prompt.append("- Maximum hours per week: ").append(input.getConstraints().get("maxHoursPerWeek")).append("\n");
        prompt.append("- Minimum rest hours between shifts: ").append(input.getConstraints().get("minRestHours")).append("\n\n");

        prompt.append("Please consider:\n");
        prompt.append("1. Employee skills and qualifications\n");
        prompt.append("2. Fair distribution of shifts\n");
        prompt.append("3. Labor law compliance\n");
        prompt.append("4. Employee preferences when possible\n\n");

        prompt.append("Return the schedule in JSON format.");

        return prompt.toString();
    }

    private List<ShiftSchedule> parseResponse(String response, AgentInput input) {
        List<ShiftSchedule> schedules = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    ShiftSchedule schedule = new ShiftSchedule();
                    schedule.setEmployeeId(node.get("employeeId").asInt());
                    schedule.setShiftDate(dateFormat.parse(node.get("shiftDate").asText()));
                    schedule.setShiftType(node.get("shiftType").asText());
                    schedule.setRequiredEmployees(input.getStaffingRequirements().get(schedule.getShiftType()));
                    schedules.add(schedule);
                }
            } else if (jsonNode.isObject()) {
                ShiftSchedule schedule = new ShiftSchedule();
                schedule.setEmployeeId(jsonNode.get("employeeId").asInt());
                schedule.setShiftDate(dateFormat.parse(jsonNode.get("shiftDate").asText()));
                schedule.setShiftType(jsonNode.get("shiftType").asText());
                schedule.setRequiredEmployees(input.getStaffingRequirements().get(schedule.getShiftType()));
                schedules.add(schedule);
            } else {
                logger.error("Unexpected JSON format from OpenAI response");
                throw new RuntimeException("Unexpected JSON format from OpenAI response");
            }
            return schedules;
        } catch (Exception e) {
            logger.error("Error parsing OpenAI response: ", e);
            throw new RuntimeException("Failed to parse shift schedule from OpenAI response", e);
        }
    }
}
