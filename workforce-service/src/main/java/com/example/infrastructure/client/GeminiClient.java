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
        prompt.append("1. One employee can only do one job per day\n");
        prompt.append("2. Strict priority-based allocation:\n");
        prompt.append("   - Priority 1 (CRITICAL) gets employees FIRST\n");
        prompt.append("   - Priority 2 (HIGH) gets employees SECOND\n");
        prompt.append("   - Priority 3 (MEDIUM) gets employees THIRD\n");
        prompt.append("   - Priority 4 (LOW) gets employees FOURTH\n");
        prompt.append("   - Priority 5 (MINIMAL) gets employees LAST\n");
        prompt.append("3. Standard 8-hour shifts starting at 8:00 AM\n");
        prompt.append("4. Each job needs 1 employee only\n");

        prompt.append("DAY-BY-DAY PLANNING REQUIREMENTS:\n");
        prompt.append("1. Calculate the total number of days needed to complete ALL jobs\n");
        prompt.append("2. Plan work day by day, ensuring optimal resource allocation\n");
        prompt.append("3. For each day, clearly assign which employee works on which job\n");
        prompt.append("4. Ensure continuous workflow - complete higher priority jobs first\n");
        prompt.append("5. Minimize total completion time while respecting priority constraints\n");
        prompt.append("6. Group the output chronologically by date (earliest dates first)\n\n");

        prompt.append("ALLOCATION LOGIC:\n");
        prompt.append("1. Calculate minimum days needed: (Total jobs × employees per job) ÷ total available employees\n");
        prompt.append("2. Sort all jobs by priority (1=highest, 5=lowest)\n");
        prompt.append("3. For each day in sequence:\n");
        prompt.append("   a. Start with highest priority unfinished jobs\n");
        prompt.append("   b. Each job just needs one employee\n");
        prompt.append("   c. Once an employee is assigned to a job on a day, they cannot work another job that same day\n");
        prompt.append("   d. Continue until all available employees are assigned or no more jobs can be started\n");
        prompt.append("   e. Move to next day and repeat\n");
        prompt.append("4. Ensure all jobs are completed within the specified time period\n\n");

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
        prompt.append("Jobs to schedule (").append(input.getJobsToSchedule().size()).append(" total, sorted by priority):\n");
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

        // Calculate estimated completion time
        int totalJobs = input.getJobsToSchedule().size();
        int employeesPerJob = getRequiredEmployeesFromInput(input);
        int totalEmployees = input.getAvailableEmployees().size();
        int estimatedDays = Math.max(1, (int) Math.ceil((double) (totalJobs * employeesPerJob) / totalEmployees));

        prompt.append("COMPLETION TIME ESTIMATION:\n");
        prompt.append("- Total jobs to complete: ").append(totalJobs).append("\n");
        prompt.append("- Employees required per job = 1");
        prompt.append("- Total available employees: ").append(totalEmployees).append("\n");
        prompt.append("- Estimated minimum days needed: ").append(estimatedDays).append("\n");
        prompt.append("- Plan should aim to complete within ").append(estimatedDays).append("-").append(estimatedDays + 1).append(" days\n\n");

        // Constraints
        if (input.getConstraints() != null) {
            prompt.append("Additional constraints:\n");
            input.getConstraints().forEach((key, value) ->
                prompt.append("- ").append(key).append(": ").append(value).append("\n")
            );
            prompt.append("\n");
        }

        prompt.append("EXAMPLE OUTPUT FORMAT (grouped by day):\n");
        prompt.append("[\n");
        prompt.append("  // Day 1 - Focus on Priority 1 jobs first\n");
        prompt.append("  {\"employeeId\": 1, \"shiftDate\": \"2025-10-20\", \"jobId\": 3, \"jobPriority\": 1},\n");
        prompt.append("  {\"employeeId\": 2, \"shiftDate\": \"2025-10-20\", \"jobId\": 3, \"jobPriority\": 1},\n");
        prompt.append("  {\"employeeId\": 3, \"shiftDate\": \"2025-10-20\", \"jobId\": 1, \"jobPriority\": 1},\n");
        prompt.append("  // Day 2 - Continue with remaining Priority 1, then Priority 2\n");
        prompt.append("  {\"employeeId\": 1, \"shiftDate\": \"2025-10-21\", \"jobId\": 1, \"jobPriority\": 1},\n");
        prompt.append("  {\"employeeId\": 2, \"shiftDate\": \"2025-10-21\", \"jobId\": 4, \"jobPriority\": 2},\n");
        prompt.append("  {\"employeeId\": 3, \"shiftDate\": \"2025-10-21\", \"jobId\": 4, \"jobPriority\": 2}\n");
        prompt.append("]\n\n");

        prompt.append("FINAL REQUIREMENTS:\n");
        prompt.append("- Sort output chronologically by shiftDate (earliest first)\n");
        prompt.append("- Within each day, sort by job priority (highest priority first)\n");
        prompt.append("- Each employee can appear only ONCE per day across all jobs\n");
        prompt.append("- Ensure all jobs are assigned the required number of employees\n");
        prompt.append("- Minimize total completion time while respecting priority order\n");
        prompt.append("- Return valid JSON array ONLY, no additional text or comments\n");

        return prompt.toString();
    }

    /**
     * get priority name from priority integer
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


    private int getRequiredEmployeesFromInput(AgentInput input) {

        return 1;
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
            // Check for required fields including jobId
            if (!node.has("employeeId") || !node.has("shiftDate") || !node.has("jobId")) {
                logger.warn("Missing required fields (employeeId, shiftDate, jobId) in schedule node: {}", node.toString());
                return null;
            }

            ShiftPlan schedule = new ShiftPlan();
            schedule.setEmployeeId(node.get("employeeId").asLong());
            schedule.setJobId(node.get("jobId").asLong());

            // Parse shift date
            try {
                schedule.setShiftDate(dateFormat.parse(node.get("shiftDate").asText()));
            } catch (Exception e) {
                logger.warn("Failed to parse shiftDate: {}", node.get("shiftDate").asText());
                // Use default date if parsing fails
                schedule.setShiftDate(input.getStartTime());
            }

            // Set job priority from the response or find from jobs list
            if (node.has("jobPriority")) {
                schedule.setJobPriority(node.get("jobPriority").asInt());
            } else {
                // Find job priority from jobs list using jobId
                Long jobId = schedule.getJobId();
                Job matchingJob = input.getJobsToSchedule().stream()
                        .filter(job -> job.getJobId().equals(jobId))
                        .findFirst()
                        .orElse(null);
                if (matchingJob != null) {
                    schedule.setJobPriority(matchingJob.getPriority());
                } else {
                    logger.warn("Could not find job with ID {} in jobs list", jobId);
                    schedule.setJobPriority(3); // Default medium priority
                }
            }

            // Set default status and version
            schedule.setStatus("PENDING_APPROVAL");
            schedule.setVersion(1);

            // Set standard 8-hour work time (8:00 AM to 4:00 PM)
            setStandardWorkTime(schedule);

            // Set required employees from input based on priority level
            String priorityLevel = getPriorityName(schedule.getJobPriority());
            Integer requiredEmployees = input.getStaffingRequirements().get(priorityLevel);
            schedule.setRequiredEmployees(requiredEmployees != null ? requiredEmployees : 1);

            logger.debug("Successfully parsed schedule: Employee {} -> Job {} (Priority {})",
                schedule.getEmployeeId(), schedule.getJobId(), schedule.getJobPriority());

            return schedule;
        } catch (Exception e) {
            logger.warn("Error parsing schedule node: {}", node.toString(), e);
            return null;
        }
    }

    /**
     * Set standard 8-hour work time for a shift plan
     */
    private void setStandardWorkTime(ShiftPlan shiftPlan) {
        if (shiftPlan.getShiftDate() == null) {
            return;
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(shiftPlan.getShiftDate());

        // Set start time to 8:00 AM
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 8);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        shiftPlan.setStartTime(calendar.getTime());

        // Set end time to 4:00 PM (8 hours later)
        calendar.add(java.util.Calendar.HOUR_OF_DAY, 8);
        shiftPlan.setEndTime(calendar.getTime());
    }
}
