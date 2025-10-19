package com.example.service;

import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftPlan;
import com.example.service.usecase.GenerateShiftPlanService;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.ExternalNewSchedule;
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
            // Fallback to REST API if ExternalNewSchedule fails
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
     * create shift plans from machine schedule data using GenerateShiftPlanService with Gemini AI
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

        // Log job information for debugging
        logJobInformation(allJobs, machineSchedule);

        // 2. Use GenerateShiftPlanService with Gemini AI to create shift plans
        try {
            Date startDate = calculateOptimalStartDate(allJobs);
            Date endDate = calculateOptimalEndDate(allJobs);

            logger.info("Using GenerateShiftPlanService with Gemini AI to create shift plans");
            logger.info("Date range: {} to {}", startDate, endDate);

            List<ShiftPlan> generatedPlans = generateShiftPlanService.generateShiftPlan(
                startDate, endDate, allJobs, defaultRequiredEmployees);

            // 3. Enhance generated plans with additional information
            enhanceShiftPlansWithJobDetails(generatedPlans, allJobs);

            logger.info("Successfully created {} shift plans using Gemini AI", generatedPlans.size());
            return generatedPlans;

        } catch (Exception e) {
            logger.error("Failed to generate shift plans with Gemini AI: {}", e.getMessage(), e);
            logger.info("Falling back to basic shift plan creation");
            return createBasicShiftPlans(allJobs);
        }
    }

    /**
     * Log job information for debugging and validation
     */
    private void logJobInformation(List<Job> allJobs, MachineSchedule machineSchedule) {
        // Sort jobs by priority for logging
        List<Job> sortedJobs = allJobs.stream()
            .sorted(Comparator.comparing(Job::getPriority, Comparator.nullsLast(Integer::compareTo)))
            .toList();

        logger.info("Total jobs to schedule: {}", sortedJobs.size());

        // Log individual job details
        for (Job job : sortedJobs) {
            String machineId = findMachineForJob(job.getJobId(), machineSchedule);
            logger.info("Job {} (Priority: {}) on Machine {}",
                job.getJobId(), job.getPriority(), machineId);
        }

        // Log mock data creation info
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

    /**
     * Enhance generated shift plans with job-specific details
     */
    private void enhanceShiftPlansWithJobDetails(List<ShiftPlan> shiftPlans, List<Job> allJobs) {
        for (ShiftPlan shiftPlan : shiftPlans) {
            // Find associated job
            Job associatedJob = findJobById(shiftPlan.getJobId(), allJobs);
            if (associatedJob != null) {
                // Ensure all required fields are set for proper output format
                enhanceShiftPlanWithJobTiming(shiftPlan, associatedJob);
            }
        }
    }

    /**
     * Enhance shift plan with proper timing based on job requirements
     */
    private void enhanceShiftPlanWithJobTiming(ShiftPlan shiftPlan, Job job) {
        // Calculate shift timing based on job requirements
        Date shiftStartDate = job.getStartDate() != null ?
            localDateToDate(job.getStartDate()) : new Date();

        // Set shift date
        shiftPlan.setShiftDate(shiftStartDate);

        // Set daily work time (8 hours shift, 8 AM to 4 PM)
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(shiftStartDate);
        startCal.set(Calendar.HOUR_OF_DAY, 8);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        shiftPlan.setStartTime(startCal.getTime());

        // Calculate end time based on job duration
        Date shiftEndDate = calculateShiftEndDate(shiftStartDate, job.getJobTimeNeededDays());
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(shiftEndDate);
        endCal.set(Calendar.HOUR_OF_DAY, 16);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        shiftPlan.setEndTime(endCal.getTime());

        logger.info("Created shift plan for Job {} - Duration: {} days, Start: {}, End: {}",
            job.getJobId(), job.getJobTimeNeededDays(),
            startCal.getTime(), endCal.getTime());
    }

    /**
     * Basic fallback shift plan creation if Gemini AI fails
     */
    private List<ShiftPlan> createBasicShiftPlans(List<Job> allJobs) {
        List<ShiftPlan> shiftPlans = new ArrayList<>();

        List<com.example.domain.model.aggregates.Employee> availableEmployees =
            employeeRepository.findAvailableEmployees();

        if (availableEmployees.isEmpty()) {
            logger.warn("No available employees found");
            return shiftPlans;
        }

        logger.info("Found {} available employees", availableEmployees.size());

        // Simple assignment: one job per available employee
        int employeeIndex = 0;
        for (Job job : allJobs) {
            if (employeeIndex >= availableEmployees.size()) {
                employeeIndex = 0; // Cycle back to first employee
            }

            com.example.domain.model.aggregates.Employee employee = availableEmployees.get(employeeIndex);
            ShiftPlan shiftPlan = createBasicShiftPlan(job, employee.getEmployeeId());
            shiftPlans.add(shiftPlan);

            employeeIndex++;
        }

        return shiftPlans;
    }

    /**
     * Create a basic shift plan for fallback scenarios
     */
    private ShiftPlan createBasicShiftPlan(Job job, Long employeeId) {
        ShiftPlan shiftPlan = new ShiftPlan();

        shiftPlan.setEmployeeId(employeeId);
        shiftPlan.setJobId(job.getJobId());
        shiftPlan.setJobPriority(job.getPriority());
        shiftPlan.setStatus("PENDING_APPROVAL");

        enhanceShiftPlanWithJobTiming(shiftPlan, job);

        return shiftPlan;
    }

    private Job findJobById(Long jobId, List<Job> allJobs) {
        return allJobs.stream()
            .filter(job -> jobId.equals(job.getJobId()))
            .findFirst()
            .orElse(null);
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

    private Date calculateShiftEndDate(Date startDate, Integer jobTimeNeededDays) {
        if (jobTimeNeededDays == null || jobTimeNeededDays <= 1) {
            return startDate;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, jobTimeNeededDays - 1);
        return cal.getTime();
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
