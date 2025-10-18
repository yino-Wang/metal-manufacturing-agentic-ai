package com.example.infrastructure.client;

import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftPlan;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.SimpleDateFormat;
import java.util.Comparator;
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
    public List<ShiftPlan> generateShiftPlan(AgentInput input) {
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

    private List<ShiftPlan> callGeminiAPI(AgentInput input) {
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
        prompt.append("You are an AI scheduling assistant specialized in creating optimal shift plans based on job priorities. ");
        prompt.append("Return ONLY a valid JSON array with the following structure: ");
        prompt.append("[{\"employeeId\": number, \"shiftDate\": \"YYYY-MM-DD\", \"jobPriority\": number}]. ");
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

        // Jobs with priorities (simplified - only priority info needed for scheduling)
        prompt.append("Jobs to schedule (by priority):\n");
        for (Job job : input.getJobsToSchedule()) {
            prompt.append("- Job ID: ").append(job.getJobId())
                    .append(", Priority: ").append(job.getPriority())
                    .append(" (").append(getPriorityLevel(job.getPriority())).append(")")
                    .append("\n");
        }
        prompt.append("\n");

        // Staffing requirements based on priority levels
        prompt.append("Staffing requirements by priority level:\n");
        for (Map.Entry<String, Integer> entry : input.getStaffingRequirements().entrySet()) {
            prompt.append("- Priority level: ").append(entry.getKey())
                    .append(", Required employees: ").append(entry.getValue())
                    .append("\n");
        }
        prompt.append("\n");

        // Constraints
        prompt.append("Constraints:\n");
        prompt.append("- Maximum hours per week: ").append(input.getConstraints().get("maxHoursPerWeek")).append("\n");
        prompt.append("- Minimum rest hours between shifts: ").append(input.getConstraints().get("minRestHours")).append("\n");
        prompt.append("- Priority-based scheduling enabled: ").append(input.getConstraints().get("priorityWeight")).append("\n\n");

        prompt.append("Priority scheduling rules (1=highest, 5=lowest):\n");
        prompt.append("1. CRITICAL priority (1): Assign best skilled employees first\n");
        prompt.append("2. HIGH priority (2): Assign experienced employees\n");
        prompt.append("3. MEDIUM priority (3): Standard employee assignment\n");
        prompt.append("4. LOW priority (4): Can use less experienced employees\n");
        prompt.append("5. MINIMAL priority (5): Use available employees efficiently\n\n");

        prompt.append("Please consider:\n");
        prompt.append("1. Job priority levels when assigning employees\n");
        prompt.append("2. Employee skills matching job requirements\n");
        prompt.append("3. Fair distribution of shifts across priority levels\n");
        prompt.append("4. Labor law compliance\n");
        prompt.append("5. Employee workload balance\n\n");

        prompt.append("Return the schedule as a JSON array ONLY. No additional text.");

        return prompt.toString();
    }

    private String getPriorityLevel(Integer priority) {
        if (priority == null) return "NORMAL";
        if (priority == 1) return "CRITICAL";
        if (priority == 2) return "HIGH";
        if (priority == 3) return "MEDIUM";
        if (priority == 4) return "LOW";
        if (priority == 5) return "MINIMAL";
        return "NORMAL"; // Default for any other values
    }

    private List<ShiftPlan> parseResponse(String response, AgentInput input) {
        List<ShiftPlan> schedules = new ArrayList<>();

        try {
            // Clean the response - remove any non-JSON content
            String cleanedResponse = extractJsonFromResponse(response);
            logger.debug("Cleaned JSON response: {}", cleanedResponse);

            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    ShiftPlan schedule = parseScheduleNode(node, input);
                    if (schedule != null) {
                        schedules.add(schedule);
                    }
                }
            } else if (jsonNode.isObject()) {
                ShiftPlan schedule = parseScheduleNode(jsonNode, input);
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

    private ShiftPlan parseScheduleNode(JsonNode node, AgentInput input) {
        try {
            if (!node.has("employeeId") || !node.has("shiftDate")) {
                logger.warn("Missing required fields in schedule node: {}", node.toString());
                return null;
            }

            ShiftPlan schedule = new ShiftPlan();
            schedule.setEmployeeId(node.get("employeeId").asLong());
            schedule.setShiftDate(dateFormat.parse(node.get("shiftDate").asText()));

            // Set job priority from the response or find from jobs list
            if (node.has("jobPriority")) {
                schedule.setJobPriority(node.get("jobPriority").asInt());
            } else {
                // Find the highest priority job from the input (lowest number = highest priority)
                Job highestPriorityJob = input.getJobsToSchedule().stream()
                        .min(Comparator.comparing(Job::getPriority))
                        .orElse(null);
                if (highestPriorityJob != null) {
                    schedule.setJobPriority(highestPriorityJob.getPriority());
                    schedule.setJobId(highestPriorityJob.getJobId());
                }
            }

            // Set required employees from input based on priority level
            String priorityLevel = getPriorityLevel(schedule.getJobPriority());
            Integer requiredEmployees = input.getStaffingRequirements().get(priorityLevel);
            schedule.setRequiredEmployees(requiredEmployees != null ? requiredEmployees : 1);

            return schedule;
        } catch (Exception e) {
            logger.warn("Error parsing schedule node: {}", node.toString(), e);
            return null;
        }
    }
}
