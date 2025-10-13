# Metal Manufacturing Agentic AI System

// ...existing code...

## How to Integrate with Workforce Service via Kafka

### For Other Microservice Developers

This section provides practical examples for developers of other microservices (Business Service, Inventory Service, Maintenance AI Service) to communicate with the Workforce Service.

#### 1. Sending Events TO Workforce Service

##### Trigger Auto Scheduling (from Business Service)
When you create a new machine schedule and need workforce assignment:

```java
@Component
public class MachineSchedulePublisher {
    
    @Autowired
    private StreamBridge streamBridge;
    
    public void publishMachineScheduleCreated(MachineSchedule schedule) {
        MachineScheduleCreatedEvent event = MachineScheduleCreatedEvent.builder()
            .jobId(schedule.getJobId())
            .machineId(schedule.getMachineId())
            .scheduledStartTime(schedule.getStartTime())
            .scheduledEndTime(schedule.getEndTime())
            .requiredSkills(Arrays.asList("CNC", "Quality Control"))
            .priority("HIGH")
            .build();
            
        // Send to Workforce Service
        streamBridge.send("machineScheduleCreated-out-0", event);
        log.info("Published machine schedule event for job: {}", schedule.getJobId());
    }
}
```

**Configuration in your service's application.properties:**
```properties
# Publisher configuration to send events to Workforce Service
spring.cloud.stream.bindings.machineScheduleCreated-out-0.destination=machine-schedule-events
```

#### 2. Receiving Events FROM Workforce Service

##### Handle Shift Published Events
When Workforce Service finalizes shift schedules, your service can react:

```java
@Component
public class WorkforceEventHandler {
    
    @Bean
    public Consumer<ShiftPublishedEvent> handleShiftPublished() {
        return event -> {
            log.info("Received shift published event: Job {}, Shift {}", 
                    event.getJobId(), event.getShiftId());
            
            // Update your business logic
            updateJobWithAssignedEmployees(event.getJobId(), event.getEmployeeIds());
            
            // Notify other systems if needed
            notifyProductionPlanning(event);
        };
    }
    
    private void updateJobWithAssignedEmployees(Long jobId, List<Long> employeeIds) {
        // Your business logic here
        log.info("Job {} now has {} employees assigned", jobId, employeeIds.size());
    }
}
```

##### Handle Timesheet Events (for payroll/attendance systems)
```java
@Component
public class TimesheetEventHandler {
    
    @Bean
    public Consumer<TimesheetEvent> handleTimesheetEvent() {
        return event -> {
            log.info("Received timesheet: Employee {} worked {} hours on {}", 
                    event.getEmployeeId(), event.getHoursWorked(), event.getDate());
            
            // Update payroll system
            payrollService.recordWorkHours(event.getEmployeeId(), 
                                         event.getHoursWorked(), 
                                         event.getDate());
        };
    }
}
```

**Configuration in your service's application.properties:**
```properties
# Consumer configuration to receive events from Workforce Service
spring.cloud.stream.bindings.handleShiftPublished-in-0.destination=shift-published-events
spring.cloud.stream.bindings.handleShiftPublished-in-0.group=your-service-group

spring.cloud.stream.bindings.handleTimesheetEvent-in-0.destination=timesheet-events
spring.cloud.stream.bindings.handleTimesheetEvent-in-0.group=your-service-group
```

#### 3. Event Schemas Reference

##### Events YOU Send TO Workforce Service:

**MachineScheduleCreatedEvent** (Topic: `machine-schedule-events`)
```json
{
  "jobId": 123,
  "machineId": "CNC-001",
  "scheduledStartTime": "2025-10-14T08:00:00Z",
  "scheduledEndTime": "2025-10-14T16:00:00Z",
  "requiredSkills": ["CNC", "Quality Control"],
  "priority": "HIGH"
}
```

##### Events YOU Receive FROM Workforce Service:

**ShiftPublishedEvent** (Topic: `shift-published-events`)
```json
{
  "shiftId": 456,
  "employeeIds": [101, 102, 103],
  "shiftDate": "2025-10-14",
  "shiftType": "DAY_SHIFT",
  "jobId": 123,
  "status": "CONFIRMED",
  "createdBy": "AI_SCHEDULER",
  "createdAt": "2025-10-13T15:30:00Z"
}
```

**TimesheetEvent** (Topic: `timesheet-events`)
```json
{
  "employeeId": 101,
  "date": "2025-10-14",
  "hoursWorked": 8.0,
  "shiftType": "DAY_SHIFT",
  "jobId": 123,
  "status": "COMPLETED"
}
```

#### 4. Complete Integration Example

Here's a complete example for a Business Service that needs workforce scheduling:

```java
@Service
public class ProductionJobService {
    
    @Autowired
    private StreamBridge streamBridge;
    
    @Autowired
    private JobRepository jobRepository;
    
    // Step 1: Create job and request workforce
    public Job createProductionJob(JobRequest request) {
        Job job = new Job(request);
        job = jobRepository.save(job);
        
        // Request workforce from Workforce Service
        requestWorkforceForJob(job);
        
        return job;
    }
    
    private void requestWorkforceForJob(Job job) {
        MachineScheduleCreatedEvent event = MachineScheduleCreatedEvent.builder()
            .jobId(job.getId())
            .machineId(job.getMachineId())
            .scheduledStartTime(job.getStartTime())
            .scheduledEndTime(job.getEndTime())
            .requiredSkills(job.getRequiredSkills())
            .priority(job.getPriority())
            .build();
            
        streamBridge.send("machineScheduleCreated-out-0", event);
    }
    
    // Step 2: Handle workforce assignment response
    @Bean
    public Consumer<ShiftPublishedEvent> handleWorkforceAssigned() {
        return event -> {
            Optional<Job> jobOpt = jobRepository.findById(event.getJobId());
            if (jobOpt.isPresent()) {
                Job job = jobOpt.get();
                job.setAssignedEmployees(event.getEmployeeIds());
                job.setStatus(JobStatus.WORKFORCE_ASSIGNED);
                jobRepository.save(job);
                
                log.info("Job {} now has workforce assigned: {}", 
                        job.getId(), event.getEmployeeIds());
                
                // Continue with production planning...
                scheduleProduction(job);
            }
        };
    }
}
```

#### 5. Testing Your Integration

**Test Event Publishing:**
```java
@SpringBootTest
class KafkaIntegrationTest {
    
    @Autowired
    private StreamBridge streamBridge;
    
    @Test
    void testPublishMachineScheduleEvent() {
        MachineScheduleCreatedEvent event = new MachineScheduleCreatedEvent();
        event.setJobId(123L);
        event.setMachineId("TEST-001");
        // ... set other fields
        
        streamBridge.send("machineScheduleCreated-out-0", event);
        
        // Verify event was sent (use test containers or embedded Kafka)
    }
}
```

#### 6. Required Dependencies

Add these to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kafka</artifactId>
</dependency>
```

#### 7. Quick Setup Checklist

- [ ] Add Kafka dependencies to your service
- [ ] Configure Kafka broker connection
- [ ] Create event publisher for sending to Workforce Service
- [ ] Create event consumers for receiving from Workforce Service
- [ ] Test with actual Kafka broker
- [ ] Monitor message flow in production

#### 8. Common Integration Patterns

| Your Service | Event Flow | Purpose |
|-------------|------------|---------|
| Business Service | Send → `machine-schedule-events` | Request workforce for new jobs |
| Business Service | Receive ← `shift-published-events` | Get workforce assignments |
| Inventory Service | Receive ← `timesheet-events` | Track resource usage |
| Maintenance Service | Send → `machine-schedule-events` | Request workforce for maintenance |

This integration approach ensures loose coupling between services while maintaining real-time communication for workforce coordination.

// ...existing code...
