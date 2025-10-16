## Workforce MS Event Integration Manual

### Listening to Machine Schedule Events from Business MS

### 1. What business MS need to do

- **Define and publish the `MachineScheduleCreated` event**  
  After the business system completes machine scheduling, publish a `MachineScheduleCreated` event to the Kafka topic `machine-schedule-events`.

#### Event Structure (Java Example)
can look my MachineScheduleCreated class
if you have another attribute, please let me know, i will update it
```java
public class MachineScheduleCreated {
    private Long scheduleId;
    private String machineId;
    private String productionLine;
    private Date startTime;
    private Date endTime;
    private String shiftType;
    private int requiredEmployees;
    private String skillRequirements;
    // ... getter/setter ...
}
```

#### Event Publishing Example
we need to use streamBridge to publish event that workforce ms can listen
```java
@Autowired
private StreamBridge streamBridge;

public void publishMachineSchedule(MachineScheduleCreated event) {
    streamBridge.send("machineScheduleCreated-out-0", event);
}
```

#### Kafka Configuration Requirements

- Topic name: `machine-schedule-events`
- Message format: JSON, fields consistent with the above structure

### 2. How Workforce MS listens

- Workforce MS is already configured to consume the `machine-schedule-events` topic. Once the event is received, it will be processed automatically. No further action is required from you.

### 3. Event Field Description
just for your reference, we can add more if needed
- `scheduleId`: Unique schedule identifier
- `machineId`: Machine number
- `productionLine`: Production line name
- `startTime`/`endTime`: Schedule start and end time
- `shiftType`: Shift type
- `requiredEmployees`: Number of required employees
- `skillRequirements`: Skill requirements



