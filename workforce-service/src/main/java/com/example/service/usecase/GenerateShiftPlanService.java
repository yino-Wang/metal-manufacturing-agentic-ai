package com.example.service.usecase;

import com.example.domain.event.ShiftPublished;
import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.aggregates.Job;
import com.example.domain.model.entities.ShiftPlan;
import com.example.domain.model.commands.GenerateShiftPlanCommand;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.ShiftPlanRepository;
import com.example.infrastructure.client.GeminiClient;
import com.example.infrastructure.messaging.ShiftPublishedEventPublisher;
import com.example.service.DTO.AutoScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// Service for generating shift plans for employees based on job priorities using Gemini AI
@Service
public class GenerateShiftPlanService {
    private final EmployeeRepository employeeRepository;
    private final ShiftPlanRepository shiftPlanRepository;
    private final GeminiClient geminiClient;
    private final ShiftPublishedEventPublisher shiftPublishedEventPublisher;
    private final EmployeeNotificationService employeeNotificationService;

    @Autowired
    public GenerateShiftPlanService(
            EmployeeRepository employeeRepository,
            ShiftPlanRepository shiftPlanRepository,
            GeminiClient geminiClient,
            ShiftPublishedEventPublisher shiftPublishedEventPublisher,
            EmployeeNotificationService employeeNotificationService) {
        this.employeeRepository = employeeRepository;
        this.shiftPlanRepository = shiftPlanRepository;
        this.geminiClient = geminiClient;
        this.shiftPublishedEventPublisher = shiftPublishedEventPublisher;
        this.employeeNotificationService = employeeNotificationService;
    }

    // Generate shift plan using  Google Gemini AI
    public List<ShiftPlan> generateShiftPlan(Date startDate,
                                             Date endDate,
                                             List<Job> jobsToSchedule,
                                             int requiredEmployees) {
        // 1. Find available employees
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();

        if (availableEmployees.isEmpty()) {
            throw new RuntimeException("No available employees");
        }

        System.out.println("DEBUG: Using ONLY Gemini AI for shift plan generation");

        AgentInput input = createAgentInputWithRequirements(startDate, endDate, jobsToSchedule, availableEmployees, requiredEmployees);
        List<ShiftPlan> generatedPlans = geminiClient.generateShiftPlan(input);

        // 3. process Gemini AI generated results
        List<ShiftPlan> processedPlans = processGeminiResults(generatedPlans, jobsToSchedule);

        // 4. Save and publish events
        List<ShiftPlan> savedSchedules = shiftPlanRepository.saveAll(processedPlans);
        for (ShiftPlan s : savedSchedules) {
            shiftPublishedEventPublisher.publish(new ShiftPublished(s));
        }

        System.out.println("DEBUG: Gemini AI generated " + savedSchedules.size() + " shift plans successfully");

        return savedSchedules;
    }

    /**
     * transform input data to AgentInput
     */
    private AgentInput createAgentInputWithRequirements(Date startDate, Date endDate, List<Job> jobsToSchedule,
                                                      List<Employee> availableEmployees, int requiredEmployees) {
        AgentInput input = new AgentInput();
        input.setAvailableEmployees(availableEmployees);
        input.setStartTime(startDate);
        input.setEndTime(endDate);
        input.setJobsToSchedule(jobsToSchedule);

        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);
        constraints.put("priorityWeight", true);
        constraints.put("oneJobPerEmployeePerDay", true);
        constraints.put("strictPriorityOrder", true);
        constraints.put("standardWorkHours", 8);
        constraints.put("workStartTime", "08:00");
        input.setConstraints(constraints);


        Map<String, Integer> staffingRequirements = new HashMap<>();
        for (Job job : jobsToSchedule) {
            String priorityLevel = getPriorityLevel(job.getPriority());
            staffingRequirements.put(priorityLevel, requiredEmployees);
        }
        input.setStaffingRequirements(staffingRequirements);

        return input;
    }

    /**
     * process Gemini AI generated
     */
    private List<ShiftPlan> processGeminiResults(List<ShiftPlan> generatedPlans, List<Job> jobsToSchedule) {
        List<ShiftPlan> processedPlans = new ArrayList<>();

        for (ShiftPlan plan : generatedPlans) {

            if (plan.getEmployeeId() == null || plan.getJobId() == null) {
                System.out.println("DEBUG: Skipping incomplete plan - missing employeeId or jobId");
                continue;
            }


            if (plan.getStatus() == null) {
                plan.setStatus("PENDING_APPROVAL");
            }
            if (plan.getVersion() == null) {
                plan.setVersion(1);
            }

            // get proiority from corresponding job
            Job correspondingJob = jobsToSchedule.stream()
                .filter(job -> job.getJobId().equals(plan.getJobId()))
                .findFirst()
                .orElse(null);

            if (correspondingJob != null) {
                plan.setJobPriority(correspondingJob.getPriority());
            }

            if (plan.getStartTime() == null || plan.getEndTime() == null) {
                setStandardWorkTime(plan);
                System.out.println("DEBUG: Set standard work time for Employee " + plan.getEmployeeId() +
                                 ", Job " + plan.getJobId() + ": " + plan.getStartTime() + " to " + plan.getEndTime());
            }

            processedPlans.add(plan);
        }

        System.out.println("DEBUG: Processed " + processedPlans.size() + " valid plans from Gemini AI");
        return processedPlans;
    }

    private void setStandardWorkTime(ShiftPlan shiftPlan) {
        if (shiftPlan.getShiftDate() == null) {
            shiftPlan.setShiftDate(new Date());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(shiftPlan.getShiftDate());

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        shiftPlan.setStartTime(calendar.getTime());

        calendar.add(Calendar.HOUR_OF_DAY, 8);
        shiftPlan.setEndTime(calendar.getTime());
    }

    // Helper method to convert numeric priority to priority level string
    private String getPriorityLevel(Integer priority) {
        if (priority == null) return "NORMAL";
        switch (priority) {
            case 1: return "CRITICAL";
            case 2: return "HIGH";
            case 3: return "MEDIUM";
            case 4: return "LOW";
            case 5: return "MINIMAL";
            default: return "NORMAL";
        }
    }

    // Auto-generate shift plan based on job priorities using Gemini AI
    public AutoScheduleResponse autoGenerateShiftPlan(Date startDate, Date endDate, List<Job> jobsToSchedule, int requiredEmployees) {
        AutoScheduleResponse response = new AutoScheduleResponse();
        try {
            List<ShiftPlan> schedules = generateShiftPlan(startDate, endDate, jobsToSchedule, requiredEmployees);
            response.setShiftPlans(schedules);
        } catch (RuntimeException e) {
            response.setAlternatives(new ArrayList<>());
        }
        return response;
    }

    // Backward compatibility method for old test code
    public AutoScheduleResponse autoGenerateShiftPlan(Date startDate, Date endDate, Long jobId, int requiredEmployees, String shiftType) {
        Job job = new Job();
        job.setJobId(jobId);
        job.setPriority(convertShiftTypeToPriority(shiftType));
        List<Job> jobsToSchedule = Arrays.asList(job);

        return autoGenerateShiftPlan(startDate, endDate, jobsToSchedule, requiredEmployees);
    }

    // Helper method to convert shiftType to priority
    private Integer convertShiftTypeToPriority(String shiftType) {
        if (shiftType == null) return 3;
        switch (shiftType.toUpperCase()) {
            case "CRITICAL": return 1;
            case "HIGH": return 2;
            case "MEDIUM": return 3;
            case "LOW": return 4;
            case "MINIMAL": return 5;
            default: return 3;
        }
    }

    // Manager approval: approve shift plan
    public ShiftPlan approveShiftPlan(Long scheduleId) {
        ShiftPlan schedule = shiftPlanRepository.findById(scheduleId).orElseThrow();
        schedule.setStatus("APPROVED");
        Integer currentVersion = schedule.getVersion() == null ? 1 : schedule.getVersion() + 1;
        schedule.setVersion(currentVersion);
        ShiftPlan saved = shiftPlanRepository.save(schedule);

        notifyEmployee(saved);
        return saved;
    }

    // Notification: notify employee after shift plan is published/approved
    public void notifyEmployee(ShiftPlan schedule) {
        try {
            employeeNotificationService.notifyEmployeeOfShiftAssignment(schedule, null);
            shiftPublishedEventPublisher.publish(new ShiftPublished(schedule));
        } catch (Exception e) {
            System.err.println("Failed to notify employee " + schedule.getEmployeeId() + ": " + e.getMessage());
            shiftPublishedEventPublisher.publish(new ShiftPublished(schedule));
        }
    }

    /**
     * Recommend alternative employees when shift plan generation fails
     */
    public List<Employee> recommendAlternativeEmployees(Date startDate, String skillType, float maxCostPerHour) {
        try {
            // Get all available employees
            List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();

            // Filter by cost if specified, ignore skill type
            return availableEmployees.stream()
              //  .filter(emp -> emp.getHourlyRate() == null || emp.getHourlyRate() <= maxCostPerHour)
                .limit(5) // Limit to top 5 alternatives
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting alternative employees: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Update an existing shift plan
     */
    public ShiftPlan updateShiftPlan(Long shiftPlanId, ShiftPlan updatedData) {
        ShiftPlan existingPlan = shiftPlanRepository.findById(shiftPlanId)
            .orElseThrow(() -> new RuntimeException("Shift plan not found with ID: " + shiftPlanId));

        // Update fields if provided
        if (updatedData.getEmployeeId() != null) {
            existingPlan.setEmployeeId(updatedData.getEmployeeId());
        }
        if (updatedData.getJobId() != null) {
            existingPlan.setJobId(updatedData.getJobId());
        }
        if (updatedData.getShiftDate() != null) {
            existingPlan.setShiftDate(updatedData.getShiftDate());
        }
        if (updatedData.getStartTime() != null) {
            existingPlan.setStartTime(updatedData.getStartTime());
        }
        if (updatedData.getEndTime() != null) {
            existingPlan.setEndTime(updatedData.getEndTime());
        }
        if (updatedData.getStatus() != null) {
            existingPlan.setStatus(updatedData.getStatus());
        }
        if (updatedData.getJobPriority() != null) {
            existingPlan.setJobPriority(updatedData.getJobPriority());
        }

        // Increment version
        existingPlan.setVersion((existingPlan.getVersion() != null ? existingPlan.getVersion() : 0) + 1);

        return shiftPlanRepository.save(existingPlan);
    }
}
