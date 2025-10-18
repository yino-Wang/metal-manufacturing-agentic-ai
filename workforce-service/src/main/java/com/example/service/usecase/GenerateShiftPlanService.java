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

// Service for generating shift plans for employees based on job priorities
// This service interacts with the EmployeeRepository to fetch employee details
// and the ShiftPlanRepository to save the generated shift plans.
// It includes methods to create shift plans based on job priorities and employee availability.
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

    // Method to generate shift plan using Google Gemini based on job priority
    // Generate shift plan for given date range, jobs with priorities, and required employees
    public List<ShiftPlan> generateShiftPlan(Date startDate,
                                             Date endDate,
                                             List<Job> jobsToSchedule,
                                             int requiredEmployees) {
        // 1. Find available employees
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();

        if (availableEmployees.isEmpty()) {
            List<Employee> alternatives = recommendAlternativeEmployees(startDate, "general", 25.0f);
            throw new RuntimeException("No available employees, recommended alternatives: " + alternatives);
        }

        // 2. Sort jobs by priority (lower number = higher priority, so ascending order)
        List<Job> sortedJobs = jobsToSchedule.stream()
                .sorted((j1, j2) -> Integer.compare(j1.getPriority(), j2.getPriority()))
                .collect(Collectors.toList());

        // 3. Build agent input with job priorities
        Map<String, Integer> staffingRequirements = new HashMap<>();
        for (Job job : sortedJobs) {
            String priorityLevel = getPriorityLevel(job.getPriority());
            staffingRequirements.put(priorityLevel, requiredEmployees);
        }

        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);
        constraints.put("priorityWeight", true); // Enable priority-based scheduling

        AgentInput input = new AgentInput();
        input.setAvailableEmployees(availableEmployees);
        input.setStartTime(startDate);
        input.setEndTime(endDate);
        input.setJobsToSchedule(sortedJobs);
        input.setStaffingRequirements(staffingRequirements);
        input.setConstraints(constraints);

        // 4. Call gemini to generate shift plan
        List<ShiftPlan> schedule = geminiClient.generateShiftPlan(input);

        // 5. Set additional fields based on job priorities
        for (ShiftPlan s : schedule) {
            // Find the job this shift is for and set priority-based fields
            Job associatedJob = findJobByEmployeeAndDate(sortedJobs, s);
            if (associatedJob != null) {
                s.setJobId(associatedJob.getJobId());
                s.setJobPriority(associatedJob.getPriority());
                s.setRequiredEmployees(requiredEmployees);
                s.setStatus("PENDING_APPROVAL");
            }
        }

        // 6. Save and publish events
        List<ShiftPlan> savedSchedules = shiftPlanRepository.saveAll(schedule);
        for (ShiftPlan s : savedSchedules) {
            shiftPublishedEventPublisher.publish(new ShiftPublished(s));
        }

        return savedSchedules;
    }

    /**
     * Generate shift plan using a command object (updated for job priority)
     */
    public List<ShiftPlan> generateShiftPlan(GenerateShiftPlanCommand command) {
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();
        if (availableEmployees.isEmpty()) {
            List<Employee> alternatives = recommendAlternativeEmployees(command.getStartDate(), "general", 25.0f);
            throw new RuntimeException("No available employees, recommended alternatives: " + alternatives);
        }

        // Create a single job with the priority from command
        Job commandJob = new Job();
        commandJob.setJobId(command.getJobId().longValue());
        commandJob.setPriority(command.getJobPriority());
        List<Job> jobsToSchedule = Arrays.asList(commandJob);

        Map<String, Integer> staffingRequirements = new HashMap<>();
        String priorityLevel = getPriorityLevel(command.getJobPriority());
        staffingRequirements.put(priorityLevel, command.getRequiredEmployees());

        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);
        constraints.put("priorityWeight", true);

        AgentInput input = new AgentInput();
        input.setAvailableEmployees(availableEmployees);
        input.setStartTime(command.getStartDate());
        input.setEndTime(command.getEndDate());
        input.setJobsToSchedule(jobsToSchedule);
        input.setStaffingRequirements(staffingRequirements);
        input.setConstraints(constraints);

        List<ShiftPlan> schedule = geminiClient.generateShiftPlan(input);
        for (ShiftPlan s : schedule) {
            s.setJobId(command.getJobId());
            s.setJobPriority(command.getJobPriority());
            s.setRequiredEmployees(command.getRequiredEmployees());
            s.setStatus("PENDING_APPROVAL");
        }

        List<ShiftPlan> savedSchedules = shiftPlanRepository.saveAll(schedule);
        for (ShiftPlan s : savedSchedules) {
            shiftPublishedEventPublisher.publish(new ShiftPublished(s));
        }
        return savedSchedules;
    }

    // Auto-generate shift plan based on job priorities
    public AutoScheduleResponse autoGenerateShiftPlan(Date startDate, Date endDate, List<Job> jobsToSchedule, int requiredEmployees) {
        AutoScheduleResponse response = new AutoScheduleResponse();
        try {
            List<ShiftPlan> schedules = generateShiftPlan(startDate, endDate, jobsToSchedule, requiredEmployees);
            response.setShiftPlans(schedules);
        } catch (RuntimeException e) {
            // Fetch alternative employees if no available employee
            List<Employee> alternatives = recommendAlternativeEmployees(startDate, "general", 100f);
            response.setAlternatives(alternatives);
        }
        return response;
    }

    // Backward compatibility method for old test code
    public AutoScheduleResponse autoGenerateShiftPlan(Date startDate, Date endDate, Long jobId, int requiredEmployees, String shiftType) {
        // Convert old parameters to new format
        Job job = new Job();
        job.setJobId(jobId);
        // Convert shiftType to priority
        Integer priority = convertShiftTypeToPriority(shiftType);
        job.setPriority(priority);
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

    // Helper method to convert numeric priority to priority level string
    private String getPriorityLevel(Integer priority) {
        if (priority == null) return "NORMAL";
        if (priority == 1) return "CRITICAL";
        if (priority == 2) return "HIGH";
        if (priority == 3) return "MEDIUM";
        if (priority == 4) return "LOW";
        if (priority == 5) return "MINIMAL";
        return "NORMAL"; // Default for any other values
    }

    // Helper method to find the job associated with a shift plan
    private Job findJobByEmployeeAndDate(List<Job> jobs, ShiftPlan shiftPlan) {
        // Return the highest priority job (lowest number = highest priority)
        return jobs.stream()
                .min(Comparator.comparing(Job::getPriority))
                .orElse(null);
    }

    //if employee is not available, recommend another employee with similar skills and cost constraints
    public List<Employee> recommendAlternativeEmployees(Date shiftDate, String requiredSkill, Float maxCost) {
        //find all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        //find all scheduled employees for the given date
        List<Long> scheduledEmployeeIds = shiftPlanRepository.findByShiftDate(shiftDate)
                .stream()
                .map(ShiftPlan::getEmployeeId)
                .toList();

        //filter employees with matching skills, within cost limit, and not scheduled for the date
        return allEmployees.stream()
                .filter(e -> hasRequiredSkill(e, requiredSkill))
                .filter(e -> isWithinCostLimit(e, maxCost))
                .filter(e -> !scheduledEmployeeIds.contains(e.getEmployeeId()))
                .sorted(Comparator.comparing(Employee::getPay))
                .toList();
    }

    private boolean hasRequiredSkill(Employee employee, String requiredSkill) {
        return employee.getSkill() != null &&
                employee.getSkill().contains(requiredSkill);
    }

    private boolean isWithinCostLimit(Employee employee, Float maxCost) {
        return employee.getPay() != null &&
                employee.getPay() <= maxCost;
    }

    // Manager review and adjustment: update shift plan
    // Manager can update shift plan before approval
    public ShiftPlan updateShiftPlan(Long scheduleId, ShiftPlan updatedData) {
        ShiftPlan schedule = shiftPlanRepository.findById(scheduleId).orElseThrow();
        // Update fields as needed
        schedule.setEmployeeId(updatedData.getEmployeeId());
        schedule.setShiftDate(updatedData.getShiftDate());
        schedule.setRequiredEmployees(updatedData.getRequiredEmployees());
        schedule.setJobId(updatedData.getJobId());
        schedule.setJobPriority(updatedData.getJobPriority());
        schedule.setJob(updatedData.getJob());
        schedule.setStatus("PENDING_APPROVAL");
        // Version management: increment version
        Integer currentVersion = schedule.getVersion() == null ? 1 : schedule.getVersion() + 1;
        schedule.setVersion(currentVersion);
        return shiftPlanRepository.save(schedule);
    }

    // Manager approval: approve shift plan
    public ShiftPlan approveShiftPlan(Long scheduleId) {
        ShiftPlan schedule = shiftPlanRepository.findById(scheduleId).orElseThrow();
        schedule.setStatus("APPROVED");
        // Version management: increment version
        Integer currentVersion = schedule.getVersion() == null ? 1 : schedule.getVersion() + 1;
        schedule.setVersion(currentVersion);
        ShiftPlan saved = shiftPlanRepository.save(schedule);

        // Notify employee after approval - use direct notification service
        notifyEmployee(saved);

        return saved;
    }

    // Compliance validation: check labor law and company rules
    // Validate max working hours and min rest hours
    public boolean validateCompliance(Long employeeId, Date shiftDate) {
        List<ShiftPlan> schedules = shiftPlanRepository.findByEmployeeId(employeeId);
        int totalHours = schedules.size() * 8; // Assume 8 hours per shift
        if (totalHours > 40) return false; // Max 40 hours/week
        // Check min rest hours between shifts
        schedules.sort(Comparator.comparing(ShiftPlan::getShiftDate));
        for (int i = 1; i < schedules.size(); i++) {
            long diff = schedules.get(i).getShiftDate().getTime() - schedules.get(i-1).getShiftDate().getTime();
            if (diff < 12 * 60 * 60 * 1000) return false; // Less than 12 hours rest
        }
        return true;
    }

    // Notification: notify employee after shift plan is published/approved
    // Notify employee via direct notification service and event/message
    public void notifyEmployee(ShiftPlan schedule) {
        try {
            // 1. Send direct notification to employee
            employeeNotificationService.notifyEmployeeOfShiftAssignment(schedule, null);

            // 2. Also publish event for other services (optional, keep for compatibility)
            shiftPublishedEventPublisher.publish(new ShiftPublished(schedule));

        } catch (Exception e) {
            System.err.println("Failed to notify employee " + schedule.getEmployeeId() + ": " + e.getMessage());
            // Still publish event as fallback
            shiftPublishedEventPublisher.publish(new ShiftPublished(schedule));
        }
    }

    // Exception handling: trigger approval/notification when no available employee
    // If no available employee, notify admin for manual approval
    public void handleNoAvailableEmployee(Date shiftDate, String skill, Float maxCost) {
        List<Employee> alternatives = recommendAlternativeEmployees(shiftDate, skill, maxCost);

        System.out.println("No available employees found for shift on " + shiftDate);
        System.out.println("Required skill: " + skill + ", Max cost: " + maxCost);

        if (alternatives.isEmpty()) {
            System.out.println("No alternative employees found. Connecting admin for manual staffing...");

        } else {
            System.out.println("Found " + alternatives.size() + " alternative employees:");
            for (Employee alt : alternatives) {
                System.out.println("- Employee ID: " + alt.getEmployeeId() +
                                 ", Skill: " + alt.getSkill() +
                                 ", Pay: " + alt.getPay());
            }
        }
    }
}
