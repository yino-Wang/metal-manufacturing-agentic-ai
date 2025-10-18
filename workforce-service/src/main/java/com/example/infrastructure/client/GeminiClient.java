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
import java.util.stream.Collectors;
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
        prompt.append("[{\"employeeId\": number, \"shiftDate\": \"YYYY-MM-DD\", \"jobId\": number, \"jobPriority\": number}]. ");
        prompt.append("Do not include any other text or explanation.\n\n");

        prompt.append("CRITICAL SCHEDULING REQUIREMENTS:\n");
        prompt.append("1. 一个员工一天只能做一个工作 (One employee can only do one job per day)\n");
        prompt.append("2. 严格按优先级分配 (Strict priority-based allocation):\n");
        prompt.append("   - Priority 1 (CRITICAL) gets employees FIRST\n");
        prompt.append("   - Priority 2 (HIGH) gets employees SECOND\n");
        prompt.append("   - Priority 3 (MEDIUM) gets employees THIRD\n");
        prompt.append("   - Priority 4 (LOW) gets employees FOURTH\n");
        prompt.append("   - Priority 5 (MINIMAL) gets employees LAST\n");
        prompt.append("3. 工作时间统一为8小时，上午8点开始 (Standard 8-hour shifts starting at 8:00 AM)\n");
        prompt.append("4. 每个工作需要 ").append(getRequiredEmployeesFromInput(input)).append(" 个员工\n\n");

        prompt.append("ALLOCATION LOGIC:\n");
        prompt.append("1. Sort all jobs by priority (1=highest, 5=lowest)\n");
        prompt.append("2. For each day in the time period:\n");
        prompt.append("   a. Start with highest priority jobs first\n");
        prompt.append("   b. Assign available employees to each job\n");
        prompt.append("   c. Once an employee is assigned to a job on a day, they cannot be assigned to another job that same day\n");
        prompt.append("   d. Move to next priority level only after current priority jobs are staffed\n\n");

        // Time period
        prompt.append("Time period:\n");
        prompt.append("- Start: ").append(dateFormat.format(input.getStartTime())).append("\n");
        prompt.append("- End: ").append(dateFormat.format(input.getEndTime())).append("\n\n");

        // Available employees
        prompt.append("Available employees (").append(input.getAvailableEmployees().size()).append(" total):\n");
        for (Employee emp : input.getAvailableEmployees()) {
            prompt.append("- Employee ID: ").append(emp.getEmployeeId())
                  .append(", Skill: ").append(emp.getSkill())
                  .append(", Pay: ").append(emp.getPay()).append("\n");
        }
        prompt.append("\n");

        // Jobs to schedule
        prompt.append("Jobs to schedule (sorted by priority):\n");
        List<Job> sortedJobs = input.getJobsToSchedule().stream()
            .sorted(Comparator.comparing(Job::getPriority))
            .collect(Collectors.toList());

        for (Job job : sortedJobs) {
            prompt.append("- Job ID: ").append(job.getJobId())
                  .append(", Priority: ").append(job.getPriority())
                  .append(" (").append(getPriorityName(job.getPriority())).append(")")
                  .append(", Title: ").append(job.getTitle() != null ? job.getTitle() : "N/A").append("\n");
        }
        prompt.append("\n");

        // Constraints
        if (input.getConstraints() != null) {
            prompt.append("Additional constraints:\n");
            input.getConstraints().forEach((key, value) ->
                prompt.append("- ").append(key).append(": ").append(value).append("\n")
            );
            prompt.append("\n");
        }

        prompt.append("EXAMPLE OUTPUT FORMAT:\n");
        prompt.append("[\n");
        prompt.append("  {\"employeeId\": 1, \"shiftDate\": \"2025-10-20\", \"jobId\": 3, \"jobPriority\": 1},\n");
        prompt.append("  {\"employeeId\": 2, \"shiftDate\": \"2025-10-20\", \"jobId\": 1, \"jobPriority\": 1},\n");
        prompt.append("  {\"employeeId\": 3, \"shiftDate\": \"2025-10-20\", \"jobId\": 4, \"jobPriority\": 4}\n");
        prompt.append("]\n\n");

        prompt.append("IMPORTANT: \n");
        prompt.append("- Each employee can appear only ONCE per day across all jobs\n");
        prompt.append("- Assign employees to highest priority jobs first\n");
        prompt.append("- Return valid JSON array ONLY, no additional text\n");

        return prompt.toString();
    }

    /**
     * 获取优先级名称
     */
    private String getPriorityName(Integer priority) {
        if (priority == null) return "UNKNOWN";
        switch (priority) {
            case 1: return "CRITICAL";
            case 2: return "HIGH";
            case 3: return "MEDIUM";
            case 4: return "LOW";
            case 5: return "MINIMAL";
            default: return "UNKNOWN";
        }
    }

    /**
     * 从输入中获取所需员工数量
     */
    private int getRequiredEmployeesFromInput(AgentInput input) {
        if (input.getStaffingRequirements() != null && !input.getStaffingRequirements().isEmpty()) {
            return input.getStaffingRequirements().values().iterator().next();
        }
        return 1; // 默认值
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
            String priorityLevel = getPriorityName(schedule.getJobPriority());
            Integer requiredEmployees = input.getStaffingRequirements().get(priorityLevel);
            schedule.setRequiredEmployees(requiredEmployees != null ? requiredEmployees : 1);

            return schedule;
        } catch (Exception e) {
            logger.warn("Error parsing schedule node: {}", node.toString(), e);
            return null;
        }
    }
}
