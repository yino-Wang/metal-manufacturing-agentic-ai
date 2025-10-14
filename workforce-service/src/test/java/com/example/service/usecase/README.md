<<<<<<< HEAD
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
=======
# CSCI318-Microservice-System-for-Metal-System

### Four Microservices in Metal Manufacturing System
- **Business MS**
- **Inventory MS**
- **Maintenance MS**
- **Workforce MS**

### Business MS
## Apache Kafka Setup
This Spring Boot project uses Apache Kafka as a messaging platform.
To run this project, you need to set up Kafka first.

#### Linux and MacOS
Make sure you download kafka version 3.9.- or below, as v4 does not have zookeeper.
Download a **binary package** of Apache Kafka (e.g., `kafka_2.13-3.7.0.tgz`) from
[https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
and unzip it.
In the Terminal, `cd` to the unzip folder, and start Kafka with the following commands (each in a separate Terminal session):
```bash
./bin/zookeeper-server-start.sh ./config/zookeeper.properties
```
```bash
./bin/kafka-server-start.sh ./config/server.properties
```

#### Windows
Make sure you download kafka version 3.9.- or below, as v4 does not have zookeeper.
Download a **binary package** of Apache Kafka (e.g., `kafka_2.13-3.7.0.tgz`) from
[https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
and unzip it to a directory, e.g., `C:\kafka`&mdash;Windows does not like a complex path name (!).

Use the following two commands in the Windows CMD (one in each window) to start Kafka:
```bash
C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
```
```bash
C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
```

## Run The Application
### Stream Processing and Interactive Query
After starting Kafka, run `business-service`'s main class, this main class will create 4 machines and
will continuously assign random jobs to random machines.

The following REST API is provided to query the results:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/streamquery/windowedSchedulesByAmount
```

### Adding Jobs and Machine scheduling
#### Windows CMD

Schedule a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"schedulingId\":\"Machine1\",\"employeeName\":\"John Smith\"}" http://localhost:8787/machinescheduling
```
Get all scheduledIds of all machines:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/machinescheduling/findAllSchedulingIds
```
Find a machine from a schedulingId - also shows all allocated jobs (replace `<<schedulingId>>` with the returned book key):
```shell
set schedulingId=<<schedulingId>>
```
```shell
curl "http://localhost:8787/machinescheduling/findMachine?schedulingId=%schedulingId%"
```
Add a new job to a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"machineName\":\"machine1\",\"jobNumber\":1000,\"materialNeeded\":\"wood\",\"materialAmount\":23}" http://localhost:8787/addJobToMachine
```
Find all the jobs of a specific machine via schedulingId
```shell
set schedulingId=<<schedulingId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findJobsBySchedulingId?schedulingId=%schedulingId%"
```
Find the first scheduled job of a specific machine via schedulingId
```shell
set schedulingId=<<schedulingId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findCurrentJobBySchedulingId?schedulingId=%schedulingId%"
```
Loaded onto carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHKG\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-08-25\",\"voyageNumber\":\"0100S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHGH\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-08-28\",\"voyageNumber\":\"0100S\"}" http://localhost:8786/cargohandling
```
Loaded onto next carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHGH\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-09-01\",\"voyageNumber\":\"0101S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"JNTKO\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-09-10\",\"voyageNumber\":\"0101S\"}" http://localhost:8786/cargohandling
```
Loaded onto next carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"JNTKO\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-09-15\",\"voyageNumber\":\"0102S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-09-25\",\"voyageNumber\":\"0102S\"}" http://localhost:8786/cargohandling
```
Customs:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"CUSTOMS\",\"completionTime\":\"2019-09-26\",\"voyageNumber\":\"\"}" http://localhost:8786/cargohandling
```
Claimed:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"CLAIM\",\"completionTime\":\"2019-09-28\",\"voyageNumber\":\"\"}" http://localhost:8786/cargohandling
```
<!--
(windows)
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingAmount\":20,\"originLocation\":\"HK\",\"destLocation\":\"NY\",\"destArrivalDeadline\":\"2010-08-01\"}" http://localhost:8787/cargobooking
```
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/cargobooking/findAllBookingIds
```
-->


### View Booking Event Stream
After running the `business-service`'s main class, check the Kafka topics with the following command:

(Linux/MacOS)
```shell
./bin/kafka-topics.sh --bootstrap-server=localhost:9092 --list
```
(Windows)
```shell
C:\kafka\bin\windows\kafka-topics.bat --bootstrap-server=localhost:9092 --list
```
You should see three topics. You can read data in the `cargobookings` topic:

(Linux/MacOS)
```shell
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cargobookings --from-beginning
```
(Windows)
```shell
c:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic cargobookings --from-beginning
```


### Trouble Shooting
If you cannot start Kafka, try to clean up data in the Kafka topics to start over.
For this purpose, in Linux/MacOS, delete the folders `/tmp/zookeeper`, `/tmp/kafka-logs`
and `/tmp/kafka-streams` (if any). In Windows, delete the folders `C:\tmp\zookeeper`,
`C:\tmp\kafka-logs` and `C:\kafka\kafka-streams` (if any).

>>>>>>> main
