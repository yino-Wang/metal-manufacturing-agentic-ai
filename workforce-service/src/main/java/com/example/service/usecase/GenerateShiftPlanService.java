package com.example.service.usecase;

import com.example.domain.event.ShiftPublished;
import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.aggregates.Employee;
import com.example.domain.model.entities.ShiftSchedule;
import com.example.domain.model.commands.GenerateShiftPlanCommand;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.ShiftPlanRepository;
import com.example.infrastructure.client.OpenAIClient;
import com.example.infrastructure.messaging.ShiftPublishedEventPublisher;
import com.example.service.DTO.AutoScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// Service for generating shift plans for employees
// This service interacts with the EmployeeRepository to fetch employee details
// and the ShiftPlanRepository to save the generated shift plans.
// It includes methods to create weekly or monthly shift plans based on employee availability and workload.
//It should use agent ai
@Service
public class GenerateShiftPlanService {
    private final EmployeeRepository employeeRepository;
    private final ShiftPlanRepository shiftPlanRepository;
    private final OpenAIClient openAIClient;
    private final ShiftPublishedEventPublisher shiftPublishedEventPublisher;
    private final EmployeeNotificationService employeeNotificationService;

    @Autowired
    public GenerateShiftPlanService(
            EmployeeRepository employeeRepository,
            ShiftPlanRepository shiftPlanRepository,
            OpenAIClient openAIClient,
            ShiftPublishedEventPublisher shiftPublishedEventPublisher,
            EmployeeNotificationService employeeNotificationService) {
        this.employeeRepository = employeeRepository;
        this.shiftPlanRepository = shiftPlanRepository;
        this.openAIClient = openAIClient;
        this.shiftPublishedEventPublisher = shiftPublishedEventPublisher;
        this.employeeNotificationService = employeeNotificationService;
    }

    // Method to generate shift plan using OpenAI
    // Generate shift plan for given date range, job, required employees, and shift type
    public List<ShiftSchedule> generateShiftPlan(Date startDate,
                                           Date endDate,
                                           Long jobId,
                                           int requiredEmployees,
                                           String shiftType) {
        // 1. find available employees
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();

        if (availableEmployees.isEmpty()) {
            //------------------------------need to change---------------------------------
            List<Employee> alternatives = recommendAlternativeEmployees(startDate, "general", 25.0f);
            throw new RuntimeException("No available employee, need alternatives：" + alternatives);
        }
        // 2. build agent input
        Map<String, Integer> staffingRequirements = new HashMap<>();
        staffingRequirements.put(shiftType, requiredEmployees);

        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);

        AgentInput input = new AgentInput();
        input.setAvailableEmployees(availableEmployees);
        input.setStartTime(startDate);
        input.setEndTime(endDate);
        input.setEmployeeRequirements(staffingRequirements);
        input.setConstraints(constraints);

        // 3. call OpenAI to generate shift plan
        List<ShiftSchedule> schedule = openAIClient.generateShiftPlan(input);

        // 4. set additional fields
        for (ShiftSchedule s : schedule) {
            s.setJobId(jobId);
            s.setShiftType(shiftType);
            s.setRequiredEmployees(requiredEmployees);
            s.setStatus("PENDING_APPROVAL");
        }

        // 5. save and publish event
        List<ShiftSchedule> savedSchedules = shiftPlanRepository.saveAll(schedule);
        for (ShiftSchedule s : savedSchedules) {
            shiftPublishedEventPublisher.publish(new ShiftPublished(s));
        }

        return savedSchedules;
    }

    /**
     * Generate shift plan using a command object (recommended for API integration)
     */
    public List<ShiftSchedule> generateShiftPlan(GenerateShiftPlanCommand command) {
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployees();
        if (availableEmployees.isEmpty()) {
            List<Employee> alternatives = recommendAlternativeEmployees(command.getStartDate(), command.getShiftType(), 25.0f);
            throw new RuntimeException("No available employees, recommended alternatives: " + alternatives);
        }
        Map<String, Integer> staffingRequirements = new HashMap<>();
        staffingRequirements.put(command.getShiftType(), command.getRequiredEmployees());
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxHoursPerWeek", 40);
        constraints.put("minRestHours", 12);
        AgentInput input = new AgentInput();
        input.setAvailableEmployees(availableEmployees);
        input.setStartTime(command.getStartDate());
        input.setEndTime(command.getEndDate());
        input.setEmployeeRequirements(staffingRequirements);
        input.setConstraints(constraints);
        List<ShiftSchedule> schedule = openAIClient.generateShiftPlan(input);
        for (ShiftSchedule s : schedule) {
            s.setJobId(command.getJobId());
            s.setShiftType(command.getShiftType());
            s.setRequiredEmployees(command.getRequiredEmployees());
            s.setStatus("PENDING_APPROVAL");
        }
        List<ShiftSchedule> savedSchedules = shiftPlanRepository.saveAll(schedule);
        for (ShiftSchedule s : savedSchedules) {
            shiftPublishedEventPublisher.publish(new ShiftPublished(s));
        }
        return savedSchedules;
    }

    // Auto-generate shift plan and handle no available employee scenario
    // If no available employee, return alternative suggestions
    public AutoScheduleResponse autoGenerateShiftPlan(Date startDate, Date endDate, Long jobId, int requiredEmployees, String shiftType) {
        AutoScheduleResponse response = new AutoScheduleResponse();
        try {
            List<ShiftSchedule> schedules = generateShiftPlan(startDate, endDate, jobId, requiredEmployees, shiftType);
            response.setShiftSchedule(schedules);
        } catch (RuntimeException e) {
            // fetch alternative employees if no available employee
            List<Employee> alternatives = recommendAlternativeEmployees(startDate, shiftType, 100f);
            response.setAlternatives(alternatives);
        }
        return response;
    }

    //if employee is not available, recommend another employee with similar skills and cost constraints
    public List<Employee> recommendAlternativeEmployees(Date shiftDate, String requiredSkill, Float maxCost) {
        //find all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        //find all scheduled employees for the given date
        List<Long> scheduledEmployeeIds = shiftPlanRepository.findByShiftDate(shiftDate)
                .stream()
                .map(ShiftSchedule::getEmployeeId)
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
    public ShiftSchedule updateShiftPlan(Long scheduleId, ShiftSchedule updatedData) {
        ShiftSchedule schedule = shiftPlanRepository.findById(scheduleId).orElseThrow();
        // Update fields as needed
        schedule.setShiftType(updatedData.getShiftType());
        schedule.setEmployeeId(updatedData.getEmployeeId());
        schedule.setShiftDate(updatedData.getShiftDate());
        schedule.setRequiredEmployees(updatedData.getRequiredEmployees());
        schedule.setJobId(updatedData.getJobId());
        schedule.setJob(updatedData.getJob());
        schedule.setStatus("PENDING_APPROVAL");
        // Version management: increment version
        Integer currentVersion = schedule.getVersion() == null ? 1 : schedule.getVersion() + 1;
        schedule.setVersion(currentVersion);
        return shiftPlanRepository.save(schedule);
    }

    // Manager approval: approve shift plan
    public ShiftSchedule approveShiftPlan(Long scheduleId) {
        ShiftSchedule schedule = shiftPlanRepository.findById(scheduleId).orElseThrow();
        schedule.setStatus("APPROVED");
        // Version management: increment version
        Integer currentVersion = schedule.getVersion() == null ? 1 : schedule.getVersion() + 1;
        schedule.setVersion(currentVersion);
        ShiftSchedule saved = shiftPlanRepository.save(schedule);

        // Notify employee after approval - use direct notification service
        notifyEmployee(saved);

        return saved;
    }

    // Compliance validation: check labor law and company rules
    // Validate max working hours and min rest hours
    public boolean validateCompliance(Long employeeId, Date shiftDate) {
        List<ShiftSchedule> schedules = shiftPlanRepository.findByEmployeeId(employeeId);
        int totalHours = schedules.size() * 8; // Assume 8 hours per shift
        if (totalHours > 40) return false; // Max 40 hours/week
        // Check min rest hours between shifts
        schedules.sort(Comparator.comparing(ShiftSchedule::getShiftDate));
        for (int i = 1; i < schedules.size(); i++) {
            long diff = schedules.get(i).getShiftDate().getTime() - schedules.get(i-1).getShiftDate().getTime();
            if (diff < 12 * 60 * 60 * 1000) return false; // Less than 12 hours rest
        }
        return true;
    }

    // Notification: notify employee after shift plan is published/approved
    // Notify employee via direct notification service and event/message
    public void notifyEmployee(ShiftSchedule schedule) {
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
