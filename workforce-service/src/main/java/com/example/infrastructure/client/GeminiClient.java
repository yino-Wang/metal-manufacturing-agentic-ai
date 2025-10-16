package com.example.infrastructure.client;

import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.aggregates.Employee;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeminiClient implements LLMClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final SimpleDateFormat dateFormat;

    @Autowired
    public GeminiClient(ChatModel chatModel, ObjectMapper objectMapper, SimpleDateFormat dateFormat) {

        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.dateFormat = dateFormat;
    }


    @Override
    public List<ShiftSchedule> generateShiftPlan(AgentInput input) {
        if (chatModel == null) {
            throw new RuntimeException("Google Gemini chat model is not configured. Please check langchain4j.google-ai-gemini configuration in application.properties");
        }

        int maxRetries = 3;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Attempting to call Google Gemini API (attempt {}/{})", attempt, maxRetries);
                return callGeminiAPI(input);
            } catch (Exception e) {
                lastException = e;
                logger.warn("Google Gemini API call attempt {} failed: {}", attempt, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        // Exponential backoff: wait 2^attempt seconds
                        Thread.sleep(2000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry wait", ie);
                    }
                }
            }
        }

        logger.error("All {} attempts to call Google Gemini API failed", maxRetries);
        throw new RuntimeException("Failed to generate shift plan after " + maxRetries + " attempts", lastException);
    }

    private List<ShiftSchedule> callGeminiAPI(AgentInput input) {
        try {
            String prompt = buildPrompt(input);
            logger.info("Sending prompt to Google Gemini (length: {} characters)", prompt.length());
            logger.debug("Full prompt: {}", prompt);

            String response = chatModel.chat(prompt).toString();
            logger.info("Received response from Google Gemini (length: {} characters)", response.length());
            logger.debug("Full response: {}", response);

            return parseResponse(response, input);

        } catch (Exception e) {
            logger.error("Error in Google Gemini API call: ", e);
            throw new RuntimeException("Google Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(AgentInput input) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI scheduling assistant specialized in creating optimal shift plans. ");
        prompt.append("Return ONLY a valid JSON array with the following structure: ");
        prompt.append("[{\"employeeId\": number, \"shiftDate\": \"YYYY-MM-DD\", \"shiftType\": \"string\"}]. ");
        prompt.append("Do not include any other text or explanation.\n\n");

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

        prompt.append("Return the schedule as a JSON array ONLY. No additional text.");

        return prompt.toString();
    }

    private List<ShiftSchedule> parseResponse(String response, AgentInput input) {
        List<ShiftSchedule> schedules = new ArrayList<>();

        try {
            // Clean the response - remove any non-JSON content
            String cleanedResponse = extractJsonFromResponse(response);
            logger.debug("Cleaned JSON response: {}", cleanedResponse);

            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    ShiftSchedule schedule = parseScheduleNode(node, input);
                    if (schedule != null) {
                        schedules.add(schedule);
                    }
                }
            } else if (jsonNode.isObject()) {
                ShiftSchedule schedule = parseScheduleNode(jsonNode, input);
                if (schedule != null) {
                    schedules.add(schedule);
                }
            } else {
                logger.error("Unexpected JSON format from Google Gemini response");
                throw new RuntimeException("Unexpected JSON format from Google Gemini response");
            }

            logger.info("Successfully parsed {} schedule(s) from Google Gemini response", schedules.size());
            return schedules;

        } catch (Exception e) {
            logger.error("Error parsing Google Gemini response: {}", response, e);
            throw new RuntimeException("Failed to parse shift schedule from Google Gemini response: " + e.getMessage(), e);
        }
    }

    private String extractJsonFromResponse(String response) {
        // Remove markdown formatting and extra text
        String cleaned = response.trim();

        // Remove markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        // Find the JSON array/object
        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');

        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        // Try to find JSON object
        start = cleaned.indexOf('{');
        end = cleaned.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        return cleaned.trim();
    }

    private ShiftSchedule parseScheduleNode(JsonNode node, AgentInput input) {
        try {
            if (!node.has("employeeId") || !node.has("shiftDate") || !node.has("shiftType")) {
                logger.warn("Missing required fields in schedule node: {}", node.toString());
                return null;
            }

            ShiftSchedule schedule = new ShiftSchedule();
            schedule.setEmployeeId(node.get("employeeId").asLong());
            schedule.setShiftDate(dateFormat.parse(node.get("shiftDate").asText()));
            schedule.setShiftType(node.get("shiftType").asText());

            // Set required employees from input
            Integer requiredEmployees = input.getStaffingRequirements().get(schedule.getShiftType());
            schedule.setRequiredEmployees(requiredEmployees != null ? requiredEmployees : 1);

            return schedule;
        } catch (Exception e) {
            logger.warn("Error parsing schedule node: {}", node.toString(), e);
            return null;
        }
    }
}
