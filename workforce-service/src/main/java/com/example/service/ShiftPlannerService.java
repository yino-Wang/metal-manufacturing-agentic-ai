package com.example.service;

import com.example.shared.MachineSchedule;
import com.example.shared.JobDto;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftPlan;
import com.example.service.usecase.GenerateShiftPlanService;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.ExternalMachineSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftPlannerService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftPlannerService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final GenerateShiftPlanService generateShiftPlanService;
    private final EmployeeRepository employeeRepository;
    private final ExternalMachineSchedule externalMachineSchedule;

    public ShiftPlannerService(GenerateShiftPlanService generateShiftPlanService,
                              EmployeeRepository employeeRepository,
                              ExternalMachineSchedule externalMachineSchedule) {
        this.generateShiftPlanService = generateShiftPlanService;
        this.employeeRepository = employeeRepository;
        this.externalMachineSchedule = externalMachineSchedule;
    }

    /**
     * fetch machine schedule from business-service using ExternalMachineSchedule (preferred method)
     */
    public MachineSchedule fetchMachineScheduleFromBusiness() {
        logger.info("Fetching machine schedule using ExternalMachineSchedule");
        try {
            return externalMachineSchedule.fetchNewSchedule();
        } catch (Exception e) {
            logger.warn("Failed to fetch schedule via ExternalMachineSchedule, falling back to REST API: {}", e.getMessage());
            String url = " "; //business-service machineSchedule endpoint todo
            return restTemplate.getForObject(url, MachineSchedule.class);
        }
    }


    /**
     * create shift plans from machine schedule data using Gemini AI
     */
    public List<ShiftPlan> createShiftPlansFromSchedule(MachineSchedule machineSchedule, int defaultRequiredEmployees) {
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


        Date startDate = calculateOptimalStartDate(allJobs);
        Date endDate = calculateOptimalEndDate(allJobs);

        logger.info("Using Gemini AI for shift plan generation");
        logger.info("Date range: {} to {}", startDate, endDate);
        logger.info("Jobs to schedule: {}", allJobs.size());
        logger.info("Required employees per job: {}", defaultRequiredEmployees);

        // call Gemini AI service to generate shift plans
        List<ShiftPlan> generatedPlans = generateShiftPlanService.generateShiftPlan(
            startDate, endDate, allJobs, defaultRequiredEmployees);

        // 将 machineId 填回每个 ShiftPlan（优先调用 setter，否则反射设置字段）
        for (ShiftPlan sp : generatedPlans) {
            String machineIdStr = findMachineForJob(sp.getJobId(), machineSchedule);
            Long machineIdLong = convertMachineIdToLong(machineIdStr);

            try {
                java.lang.reflect.Method setter = ShiftPlan.class.getMethod("setMachineId", Long.class);
                setter.invoke(sp, machineIdLong);
                logger.debug("Set machineId {} for ShiftPlan {} via setter", machineIdLong, sp.getShiftPlanId());
            } catch (NoSuchMethodException nsme) {
                try {
                    java.lang.reflect.Field field = ShiftPlan.class.getDeclaredField("machineId");
                    field.setAccessible(true);
                    field.set(sp, machineIdLong);
                    logger.debug("Set machineId {} for ShiftPlan {} via field", machineIdLong, sp.getShiftPlanId());
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    logger.debug("ShiftPlan has no machineId property or cannot set it via reflection", ex);
                }
            } catch (Exception e) {
                logger.warn("Unexpected error setting machineId on ShiftPlan: {}", e.getMessage(), e);
            }
        }

        markEmployeesBusy(generatedPlans);

        logger.info("Gemini AI generated {} shift plans", generatedPlans.size());

        return generatedPlans;
    }

    private void markEmployeesBusy(List<ShiftPlan> plans) {
        if (plans == null || plans.isEmpty()) return;

        Set<Long> employeeIds = plans.stream()
                .map(ShiftPlan::getEmployeeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (employeeIds.isEmpty()) return;

        // find employees and set status to BUSY
        Iterable<com.example.domain.model.aggregates.Employee> employeesIt = employeeRepository.findAllById(employeeIds);
        List<com.example.domain.model.aggregates.Employee> employees = new ArrayList<>();
        employeesIt.forEach(employees::add);

        if (employees.isEmpty()) return;

        for (com.example.domain.model.aggregates.Employee emp : employees) {
            emp.setStatus("BUSY");
        }

        // save
        employeeRepository.saveAll(employees);

        try {
            java.lang.reflect.Method m = ShiftPlan.class.getMethod("setStatus", String.class);
            for (ShiftPlan sp : plans) {
                try { m.invoke(sp, "BUSY"); } catch (Exception ignored) {}
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Convert machine ID string to Long
     * Handles cases like "MACHINE-001" -> 1, "MACHINE-002" -> 2, etc.
     * For non-numeric machine IDs, returns a hash-based Long or 0 as fallback
     */
    private Long convertMachineIdToLong(String machineIdStr) {
        if (machineIdStr == null || machineIdStr.trim().isEmpty() || "UNKNOWN".equals(machineIdStr)) {
            return 0L;
        }

        try {
            // Try direct conversion first (in case it's already numeric)
            return Long.valueOf(machineIdStr);
        } catch (NumberFormatException e) {
            // Handle formatted machine IDs like "MACHINE-001", "MACHINE-002"
            if (machineIdStr.contains("-")) {
                String[] parts = machineIdStr.split("-");
                if (parts.length >= 2) {
                    try {
                        return Long.valueOf(parts[parts.length - 1]); // Get the numeric part
                    } catch (NumberFormatException ignored) {
                        // Fall through to hash-based approach
                    }
                }
            }

            // Fallback: use hash code to generate a consistent Long ID
            long hash = Math.abs(machineIdStr.hashCode()) % 1000000L; // Keep it reasonable
            logger.debug("Converted non-numeric machineId '{}' to Long: {}", machineIdStr, hash);
            return hash;
        }
    }

    /*
    // Commented out - ExternalMachineSchedule already provides mock data
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
    */
}
