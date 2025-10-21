package com.example;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
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

    /**
     * Fetch machine schedule from business-service
     * Currently using mock data - will be replaced with actual REST call when business-service is available
     */
    public MachineSchedule fetchNewSchedule() {
        logger.info("Fetching machine schedule from business-service");

        try {
            // TODO: Replace with actual REST call to business-service when available
            // RestTemplate restTemplate = new RestTemplate();
            // String url = "http://localhost:8787/machinescheduling/findAllMachines";
            // ResponseEntity<MachineScheduleResponse> response = restTemplate.getForEntity(url, MachineScheduleResponse.class);
            // return convertToMachineSchedule(response.getBody());

            // For now, return mock data that's compatible with your system
            logger.warn("Using mock data - business-service integration pending");
            return createMockMachineSchedule();

        } catch (Exception e) {
            logger.error("Failed to fetch machine schedule from business-service: {}", e.getMessage());
            logger.info("Falling back to mock data");
            return createMockMachineSchedule();
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
