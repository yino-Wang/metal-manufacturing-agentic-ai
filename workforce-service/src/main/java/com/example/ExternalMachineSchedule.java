package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import com.example.dto.MachineScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExternalMachineSchedule {
    private static final Logger logger = LoggerFactory.getLogger(ExternalMachineSchedule.class);

    private final RestTemplate restTemplate = new RestTemplate();


    public MachineSchedule fetchNewSchedule() throws RuntimeException {
        String url = "http://localhost:8787/addJobToMachine/findJobsByMachineId?machineId=machine2";
        logger.info("Calling API: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            logger.info("HTTP Status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String rawResponse = response.getBody();
                logger.info("Successfully received raw response from business-service");
                logger.info("Raw response (first 500 chars): {}",
                        rawResponse.length() > 500 ? rawResponse.substring(0, 500) + "..." : rawResponse);

                if (rawResponse.trim().startsWith("Machine MachineId:")) {

                    logger.info("🔍 Detected text format response, using text parser");
                    try {
                        MachineSchedule parsedSchedule = parseTextResponse(rawResponse);
                        if (parsedSchedule != null && !parsedSchedule.getSchedules().isEmpty()) {
                            logger.info("Successfully parsed text response into MachineSchedule");
                            return parsedSchedule;
                        } else {
                            logger.warn("Text parsing returned null or empty schedule");
                        }
                    } catch (Exception textParseEx) {
                        logger.error("ext parsing failed: {}", textParseEx.getMessage(), textParseEx);
                    }
                } else if (rawResponse.trim().startsWith("{") || rawResponse.trim().startsWith("[")) {
                    logger.info("🔍 Detected JSON format response, using JSON parser");
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                        MachineSchedule schedule = mapper.readValue(rawResponse, MachineSchedule.class);
                        logger.info("Successfully parsed JSON response into MachineSchedule");
                        return schedule;
                    } catch (Exception jsonParseEx) {
                        logger.error("JSON parsing failed: {}", jsonParseEx.getMessage());
                    }
                } else {
                    logger.warn("Unknown response format, response starts with: {}",
                        rawResponse.length() > 50 ? rawResponse.substring(0, 50) : rawResponse);
                }

                logger.info("All parsing attempts failed, falling back to mock data");
                return createMockMachineSchedule();

            } else {
                logger.error("Invalid response from business-service. Status: {}", response.getStatusCode());
                return createMockMachineSchedule();
            }

        } catch (Exception e) {
            logger.error("Exception while calling business-service API: {}", e.getMessage(), e);
            logger.info("Using mock data due to API call failure");
            return createMockMachineSchedule();
        }
    }

    private MachineSchedule parseTextResponse(String textResponse) {
        try {
            logger.info("Starting to parse text response...");
            MachineSchedule schedule = new MachineSchedule();
            Map<String, List<JobDto>> schedules = new HashMap<>();
            List<JobDto> jobs = new ArrayList<>();

            String machineId = "machine2";
            if (textResponse.contains("MachineId: ")) {
                String[] parts = textResponse.split("MachineId: ");
                if (parts.length > 1) {
                    String idPart = parts[1].split(":")[0].trim();
                    machineId = idPart;
                    logger.info("🔍 Extracted machine ID: {}", machineId);
                }
            }

            String[] lines = textResponse.split("\n");
            JobDto currentJob = null;
            boolean inJobSection = false;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                logger.debug("Processing line {}: {}", i, line);

                if (line.startsWith("Job ") && line.contains("PRIORITY:")) {
                    if (currentJob != null) {
                        jobs.add(currentJob);
                        logger.info("Added job {} to list", currentJob.getJobId());
                    }

                    currentJob = new JobDto();
                    inJobSection = true;

                    try {
                        String[] parts = line.split(":");
                        if (parts.length >= 3) {

                            String jobIdStr = parts[0].replace("Job", "").trim();
                            long jobId = Long.parseLong(jobIdStr);
                            currentJob.setJobId(jobId);

                            String priorityStr = parts[2].trim();
                            int priority = Integer.parseInt(priorityStr);
                            currentJob.setPriority(priority);

                            logger.info("Parsed Job ID: {}, Priority: {}", jobId, priority);
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to parse job ID or priority from: {}", line);
                        currentJob.setJobId(System.currentTimeMillis() % 1000); // 临时ID
                        currentJob.setPriority(3);
                    }

                } else if (currentJob != null && line.contains("dueDate:")) {
                    parseJobDatesImproved(line, currentJob);

                } else if (currentJob != null && line.contains("customerName:")) {
                    parseJobMaterialsImproved(line, currentJob);
                }
            }

            if (currentJob != null) {
                jobs.add(currentJob);
                logger.info("Added final job {} to list", currentJob.getJobId());
            }

            String formattedMachineId = convertToMachineFormat(machineId);
            schedules.put(formattedMachineId, jobs);
            schedule.setSchedules(schedules);

            logger.info("Successfully parsed {} jobs for machine {}", jobs.size(), formattedMachineId);

            for (JobDto job : jobs) {
                logger.info("Job {}: {} - {} (Priority: {}, Material: {} x{})",
                    job.getJobId(), job.getTitle(), job.getDueDate(),
                    job.getPriority(), job.getMaterialNeeded(), job.getMaterialAmount());
            }

            return schedule;

        } catch (Exception e) {
            logger.error("Failed to parse text response: {}", e.getMessage(), e);
            return null;
        }
    }

    private void parseJobDatesImproved(String line, JobDto job) {
        try {
            logger.debug("Parsing dates from: {}", line);

            String[] parts = line.split(",");

            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("dueDate:")) {
                    String dateStr = part.substring("dueDate:".length()).trim();
                    job.setDueDate(LocalDate.parse(dateStr));
                    logger.debug("Due date: {}", dateStr);
                } else if (part.startsWith("startDate:")) {
                    String dateStr = part.substring("startDate:".length()).trim();
                    job.setStartDate(LocalDate.parse(dateStr));
                    logger.debug("Start date: {}", dateStr);
                } else if (part.startsWith("endDate:")) {
                    String dateStr = part.substring("endDate:".length()).trim();
                    job.setEndDate(LocalDate.parse(dateStr));
                    logger.debug("End date: {}", dateStr);
                } else if (part.startsWith("requiredDuration:")) {
                    String durationStr = part.substring("requiredDuration:".length()).trim();
                    job.setJobTimeNeededDays(Integer.parseInt(durationStr));
                    logger.debug("Duration: {} days", durationStr);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse job dates from: {}, error: {}", line, e.getMessage());

            job.setDueDate(LocalDate.now().plusDays(7));
            job.setStartDate(LocalDate.now());
            job.setEndDate(LocalDate.now().plusDays(5));
            job.setJobTimeNeededDays(5);
        }
    }

    private void parseJobMaterialsImproved(String line, JobDto job) {
        try {
            logger.debug("🔧 Parsing materials from: {}", line);

            String[] parts = line.split(",");

            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("customerName:")) {
                    String customerName = part.substring("customerName:".length()).trim();
                    job.setTitle("Job for " + customerName);
                    logger.debug("Customer: {}", customerName);
                } else if (part.startsWith("materialNeeded:")) {
                    String material = part.substring("materialNeeded:".length()).trim();
                    job.setMaterialNeeded(material);
                    logger.debug("Material: {}", material);
                } else if (part.startsWith("materialAmount:")) {
                    String amountStr = part.substring("materialAmount:".length()).trim();
                    job.setMaterialAmount(Integer.parseInt(amountStr));
                    logger.debug("Amount: {}", amountStr);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse job materials from: {}, error: {}", line, e.getMessage());

            job.setMaterialNeeded("steel");
            job.setMaterialAmount(10);
            if (job.getTitle() == null) {
                job.setTitle("Manufacturing Job " + job.getJobId());
            }
        }
    }



    /**
     * Convert API response to MachineSchedule format
     */
    private MachineSchedule convertToMachineSchedule(MachineScheduleResponse response) {
        Map<String, List<JobDto>> scheduleMap = new HashMap<>();

        if (response.getSchedule() != null && response.getSchedule().getJobs() != null) {
            String machineId = response.getMachineId() != null ?
                             response.getMachineId().getMachineId() : "machine1";

            // Convert machine id to MACHINE-XXX format
            String formattedMachineId = convertToMachineFormat(machineId);

            List<JobDto> jobs = new ArrayList<>();
            for (MachineScheduleResponse.JobDetailDto apiJob : response.getSchedule().getJobs()) {
                JobDto jobDto = convertApiJobToJobDto(apiJob);
                jobs.add(jobDto);
            }

            scheduleMap.put(formattedMachineId, jobs);

            logger.info("Converted API response to MachineSchedule with {} jobs for machine {}",
                       jobs.size(), formattedMachineId);
        }

        return new MachineSchedule(scheduleMap);
    }

    /**
     * Convert API job to JobDto
     */
    private JobDto convertApiJobToJobDto(MachineScheduleResponse.JobDetailDto apiJob) {
        JobDto jobDto = new JobDto();

        jobDto.setJobId(apiJob.getJobNumber());
        jobDto.setTitle(generateJobTitle(apiJob));
        jobDto.setPriority(apiJob.getPriority());
        jobDto.setJobTimeNeededDays(apiJob.getJobTimeNeededDays());
        jobDto.setMaterialNeeded(apiJob.getMaterialNeeded());
        jobDto.setMaterialAmount(apiJob.getMaterialAmount());

        // Parse dates
        try {
            if (apiJob.getDueDate() != null) {
                jobDto.setDueDate(LocalDate.parse(apiJob.getDueDate()));
            }
            if (apiJob.getStartDate() != null) {
                jobDto.setStartDate(LocalDate.parse(apiJob.getStartDate()));
            }
            if (apiJob.getEndDate() != null) {
                jobDto.setEndDate(LocalDate.parse(apiJob.getEndDate()));
            }
        } catch (Exception e) {
            logger.warn("Failed to parse dates for job {}: {}", apiJob.getJobNumber(), e.getMessage());
            // Set default dates if parsing fails
            jobDto.setDueDate(LocalDate.now().plusDays(7));
            jobDto.setStartDate(LocalDate.now().plusDays(1));
            jobDto.setEndDate(LocalDate.now().plusDays(apiJob.getJobTimeNeededDays() != null ?
                                                       apiJob.getJobTimeNeededDays() + 1 : 3));
        }

        return jobDto;
    }

    /**
     * Generate job title from API data
     */
    private String generateJobTitle(MachineScheduleResponse.JobDetailDto apiJob) {
        if (apiJob.getCustomerName() != null && apiJob.getMaterialNeeded() != null) {
            return String.format("%s - %s Job", apiJob.getCustomerName(), apiJob.getMaterialNeeded());
        } else if (apiJob.getMaterialNeeded() != null) {
            return String.format("%s Production", apiJob.getMaterialNeeded());
        } else {
            return String.format("Job %d", apiJob.getJobNumber());
        }
    }

    /**
     * Convert machine id to MACHINE-XXX format
     */
    private String convertToMachineFormat(String machineId) {
        if (machineId == null) {
            return "MACHINE-001";
        }

        // If already in MACHINE-XXX format, return as is
        if (machineId.startsWith("MACHINE-")) {
            return machineId;
        }

        // Extract number from machine id (e.g., "machine1" -> "1")
        String numberPart = machineId.replaceAll("[^0-9]", "");
        if (numberPart.isEmpty()) {
            numberPart = "1";
        }

        try {
            int machineNumber = Integer.parseInt(numberPart);
            return String.format("MACHINE-%03d", machineNumber);
        } catch (NumberFormatException e) {
            return "MACHINE-001";
        }
    }

    /**
     * Create mock machine schedule data that matches the expected format
     * This ensures Gemini AI has proper data to work with for day-by-day planning
     */
    public MachineSchedule createMockMachineSchedule() {
        Map<String, List<JobDto>> scheduleMap = new HashMap<>();

        // Machine 1 - Critical and High priority jobs (Priority 1-2)
        List<JobDto> machine1Jobs = new ArrayList<>();
        machine1Jobs.add(createJobDto(1L, "Emergency Steel Production", 1, 2, "Steel", 100));
        machine1Jobs.add(createJobDto(2L, "High Priority Assembly", 2, 1, "Aluminum", 75));

        // Machine 2 - Medium priority jobs (Priority 3)
        List<JobDto> machine2Jobs = new ArrayList<>();
        machine2Jobs.add(createJobDto(3L, "Standard Production Run", 3, 2, "Iron", 80));

        // Machine 3 - Low and Minimal priority jobs (Priority 4-5)
        List<JobDto> machine3Jobs = new ArrayList<>();
        machine3Jobs.add(createJobDto(4L, "Training Setup", 5, 1, "Training_Materials", 8));

        scheduleMap.put("MACHINE-001", machine1Jobs);
        scheduleMap.put("MACHINE-002", machine2Jobs);
        scheduleMap.put("MACHINE-003", machine3Jobs);

        int totalJobs = scheduleMap.values().stream().mapToInt(List::size).sum();
        logger.info("Created enhanced mock machine schedule with {} machines and {} total jobs",
                   scheduleMap.size(), totalJobs);

        // Log priority distribution for Gemini planning analysis
        Map<Integer, Long> priorityCount = scheduleMap.values().stream()
            .flatMap(List::stream)
            .collect(java.util.stream.Collectors.groupingBy(
                JobDto::getPriority,
                java.util.stream.Collectors.counting()
            ));

        logger.info("Job priority distribution for Gemini day-by-day planning:");
        priorityCount.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> logger.info("  Priority {}: {} jobs", entry.getKey(), entry.getValue()));

        return new MachineSchedule(scheduleMap);
    }

    /**
     * Helper method to create JobDto objects
     */
    private JobDto createJobDto(Long jobId, String title, Integer priority, Integer daysNeeded,
                               String material, Integer amount) {
        JobDto jobDto = new JobDto();
        jobDto.setJobId(jobId);
        jobDto.setTitle(title);
        jobDto.setPriority(priority);
        jobDto.setJobTimeNeededDays(daysNeeded);

        // Set realistic dates
        jobDto.setDueDate(LocalDate.now().plusDays(7));
        jobDto.setStartDate(LocalDate.now().plusDays(1));
        jobDto.setEndDate(LocalDate.now().plusDays(daysNeeded + 1));

        jobDto.setMaterialNeeded(material);
        jobDto.setMaterialAmount(amount);

        return jobDto;
    }
}
