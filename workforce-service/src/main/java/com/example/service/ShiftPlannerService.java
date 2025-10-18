package com.example.service;

import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftPlan;
import com.example.service.usecase.GenerateShiftPlanService;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.service.ExternalNewSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShiftPlannerService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftPlannerService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final GenerateShiftPlanService generateShiftPlanService;
    private final EmployeeRepository employeeRepository;
    private final ExternalNewSchedule externalNewSchedule;

    public ShiftPlannerService(GenerateShiftPlanService generateShiftPlanService,
                              EmployeeRepository employeeRepository,
                              ExternalNewSchedule externalNewSchedule) {
        this.generateShiftPlanService = generateShiftPlanService;
        this.employeeRepository = employeeRepository;
        this.externalNewSchedule = externalNewSchedule;
    }

    /**
     * fetch machine schedule from business-service using ExternalNewSchedule (preferred method)
     */
    public MachineSchedule fetchMachineScheduleFromBusiness() {
        logger.info("Fetching machine schedule using ExternalNewSchedule");
        try {
            return externalNewSchedule.fetchNewSchedule();
        } catch (Exception e) {
            logger.warn("Failed to fetch schedule via ExternalNewSchedule, falling back to REST API: {}", e.getMessage());
            String url = " "; //business-service machineSchedule endpoint todo
            return restTemplate.getForObject(url, MachineSchedule.class);
        }
    }

    /**
     * create mock machine schedule for testing
     */
    public MachineSchedule createMockMachineSchedule() {
        Map<String, List<JobDto>> scheduleMap = new HashMap<>();

        List<JobDto> machine1Jobs = Arrays.asList(
            createMockJobDto(1L, "High", 1, 3),
            createMockJobDto(2L, "Low", 5, 2)
        );

        List<JobDto> machine2Jobs = Arrays.asList(
            createMockJobDto(3L, "Urgent", 1, 2),
            createMockJobDto(4L, "Normal", 4, 1)
        );

        scheduleMap.put("MACHINE-001", machine1Jobs);
        scheduleMap.put("MACHINE-002", machine2Jobs);

        return new MachineSchedule(scheduleMap);
    }

    /**
     * create shift plans using mock machine schedule data
     */
    public List<ShiftPlan> createShiftPlansWithMockData(int defaultRequiredEmployees) {
        logger.info("Creating shift plans using mock machine schedule data");
        MachineSchedule machineSchedule = createMockMachineSchedule();
        return createShiftPlansFromSchedule(machineSchedule, defaultRequiredEmployees);
    }

    /**
     * create shift plans by fetching machine schedule from business-service
     */
    public List<ShiftPlan> createShiftPlans(int defaultRequiredEmployees) {
        logger.info("Attempting to create shift plans from business service using ExternalNewSchedule");

        try {
            MachineSchedule machineSchedule = fetchMachineScheduleFromBusiness();
            return createShiftPlansFromSchedule(machineSchedule, defaultRequiredEmployees);
        } catch (Exception e) {
            logger.warn("Failed to fetch from business service, using mock data instead: {}", e.getMessage());
            return createShiftPlansWithMockData(defaultRequiredEmployees);
        }
    }

    /**
     * create shift plans from machine schedule data using ONLY Gemini AI
     */
    private List<ShiftPlan> createShiftPlansFromSchedule(MachineSchedule machineSchedule, int defaultRequiredEmployees) {
        if (machineSchedule == null || machineSchedule.getSchedules() == null) {
            logger.warn("No machine schedules available");
            return new ArrayList<>();
        }

        // 1. Convert JobDto to Job objects and collect all jobs
        List<Job> allJobs = new ArrayList<>();
        for (Map.Entry<String, List<JobDto>> entry : machineSchedule.getSchedules().entrySet()) {
            List<JobDto> jobDtos = entry.getValue();

            if (jobDtos != null && !jobDtos.isEmpty()) {
                for (JobDto jobDto : jobDtos) {
                    Job job = convertJobDtoToJob(jobDto);
                    allJobs.add(job);
                }
            }
        }

        logJobInformation(allJobs, machineSchedule);

        // 2. 完全使用Gemini AI生成排班计划
        Date startDate = calculateOptimalStartDate(allJobs);
        Date endDate = calculateOptimalEndDate(allJobs);

        logger.info("Using ONLY Gemini AI for shift plan generation");
        logger.info("Date range: {} to {}", startDate, endDate);
        logger.info("Jobs to schedule: {}", allJobs.size());
        logger.info("Required employees per job: {}", defaultRequiredEmployees);

        // 直接调用GenerateShiftPlanService，它完全依赖Gemini AI
        List<ShiftPlan> generatedPlans = generateShiftPlanService.generateShiftPlan(
            startDate, endDate, allJobs, defaultRequiredEmployees);

        logger.info("Gemini AI generated {} shift plans", generatedPlans.size());

        return generatedPlans;
    }

    /**
     * Log job information for debugging and validation
     */
    private void logJobInformation(List<Job> allJobs, MachineSchedule machineSchedule) {
        List<Job> sortedJobs = allJobs.stream()
            .sorted(Comparator.comparing(Job::getPriority, Comparator.nullsLast(Integer::compareTo)))
            .toList();

        logger.info("Total jobs to schedule: {}", sortedJobs.size());

        for (Job job : sortedJobs) {
            String machineId = findMachineForJob(job.getJobId(), machineSchedule);
            logger.info("Job {} (Priority: {}) on Machine {}",
                job.getJobId(), job.getPriority(), machineId);
        }

        if (machineSchedule.getSchedules() != null) {
            logger.info("Mock data created successfully, contains {} machines", machineSchedule.getSchedules().size());
            for (Map.Entry<String, List<JobDto>> entry : machineSchedule.getSchedules().entrySet()) {
                String machineId = entry.getKey();
                List<JobDto> jobs = entry.getValue();
                logger.info("  Machine {} has {} jobs", machineId, jobs.size());
                for (JobDto jobDto : jobs) {
                    logger.info("    - Job ID: {}, Priority: {}, Days needed: {}",
                        jobDto.getJobId(), jobDto.getPriority(), jobDto.getJobTimeNeededDays());
                }
            }
        }
    }

    /**
     * Find machine ID for a given job
     */
    private String findMachineForJob(Long jobId, MachineSchedule machineSchedule) {
        if (machineSchedule.getSchedules() != null) {
            for (Map.Entry<String, List<JobDto>> entry : machineSchedule.getSchedules().entrySet()) {
                String machineId = entry.getKey();
                List<JobDto> jobs = entry.getValue();
                if (jobs.stream().anyMatch(job -> jobId.equals(job.getJobId()))) {
                    return machineId;
                }
            }
        }
        return "UNKNOWN";
    }

    private Date calculateOptimalStartDate(List<Job> jobs) {
        return jobs.stream()
            .map(Job::getStartDate)
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .map(this::localDateToDate)
            .orElse(new Date());
    }

    private Date calculateOptimalEndDate(List<Job> jobs) {
        return jobs.stream()
            .map(Job::getEndDate)
            .filter(Objects::nonNull)
            .max(Comparator.naturalOrder())
            .map(this::localDateToDate)
            .orElse(Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private Job convertJobDtoToJob(JobDto jobDto) {
        Job job = new Job();

        if (jobDto.getJobId() != null) {
            job.setJobId(jobDto.getJobId());
        }

        job.setDueDate(jobDto.getDueDate());
        job.setStartDate(jobDto.getStartDate());
        job.setEndDate(jobDto.getEndDate());
        job.setJobTimeNeededDays(jobDto.getJobTimeNeededDays());
        job.setPriority(jobDto.getPriority());
        job.setTitle(jobDto.getTitle() != null ? jobDto.getTitle() : "Imported Job " + jobDto.getJobId());

        return job;
    }

    private Date localDateToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private JobDto createMockJobDto(Long jobId, String title, Integer priority, Integer daysNeeded) {
        JobDto jobDto = new JobDto();
        jobDto.setJobId(jobId);
        jobDto.setTitle(title);
        jobDto.setPriority(priority);
        jobDto.setJobTimeNeededDays(daysNeeded);
        jobDto.setDueDate(LocalDate.now());
        jobDto.setStartDate(LocalDate.now().plusDays(1));
        jobDto.setEndDate(LocalDate.now().plusDays(daysNeeded + 1));
        jobDto.setMaterialNeeded("Steel");
        jobDto.setMaterialAmount(100);
        return jobDto;
    }
}
