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
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OpenAIClient implements LLMClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    private final SimpleDateFormat dateFormat;
    private final String apiKey;

    public OpenAIClient(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        logger.info("Initializing OpenAI client with API key: {}***",
                   apiKey != null && apiKey.length() > 8 ? apiKey.substring(0, 8) : "null");

        // Create OpenAI service with timeout configuration
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.objectMapper = new ObjectMapper();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public List<ShiftSchedule> generateShiftPlan(AgentInput input) {
        // Validate API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("OpenAI API key is not configured. Please set openai.api.key in application.properties");
        }

        if (!apiKey.startsWith("sk-")) {
            throw new RuntimeException("Invalid OpenAI API key format. API key should start with 'sk-'");
        }

        int maxRetries = 3;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Attempting to call OpenAI API (attempt {}/{})", attempt, maxRetries);
                return callOpenAIAPI(input);
            } catch (Exception e) {
                lastException = e;
                logger.warn("OpenAI API call attempt {} failed: {}", attempt, e.getMessage());

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

        logger.error("All {} attempts to call OpenAI API failed", maxRetries);
        throw new RuntimeException("Failed to generate shift plan after " + maxRetries + " attempts", lastException);
    }

    private List<ShiftSchedule> callOpenAIAPI(AgentInput input) {
        try {
            String prompt = buildPrompt(input);
            logger.info("Sending prompt to OpenAI (length: {} characters)", prompt.length());
            logger.debug("Full prompt: {}", prompt);

            var completion = openAiService.createChatCompletion(
                    ChatCompletionRequest.builder()
                            .model("gpt-3.5-turbo")
                            .messages(Arrays.asList(
                                    new ChatMessage("system", "You are an AI scheduling assistant specialized in creating optimal shift plans. " +
                                            "Return ONLY a valid JSON array with the following structure: " +
                                            "[{\"employeeId\": number, \"shiftDate\": \"YYYY-MM-DD\", \"shiftType\": \"string\"}]. " +
                                            "Do not include any other text or explanation."),
                                    new ChatMessage("user", prompt)
                            ))
                            .temperature(0.3)  // Lower temperature for more consistent output
                            .maxTokens(1000)   // Limit response size
                            .build()
            );

            String response = completion.getChoices().get(0).getMessage().getContent();
            logger.info("Received response from OpenAI (length: {} characters)", response.length());
            logger.debug("Full response: {}", response);

            return parseResponse(response, input);

        } catch (Exception e) {
            logger.error("Error in OpenAI API call: ", e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage(), e);
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
                logger.error("Unexpected JSON format from OpenAI response");
                throw new RuntimeException("Unexpected JSON format from OpenAI response");
            }

            logger.info("Successfully parsed {} schedule(s) from OpenAI response", schedules.size());
            return schedules;

        } catch (Exception e) {
            logger.error("Error parsing OpenAI response: {}", response, e);
            throw new RuntimeException("Failed to parse shift schedule from OpenAI response: " + e.getMessage(), e);
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
