# CSCI318-Microservice-System-for-Metal-System

### Four Microservices in Metal Manufacturing System
- **Business MS** 
- **Inventory MS** 
- **Maintenance MS** 
- **Workforce MS** 

# Business MS

## Set-up for Business MS 
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

## FR6: Handling newly submitted jobs (real-time) part 1
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
curl -X GET -H "Content-Type:application/json" http://localhost:8787/queries/windowedMachinesByAmount
```

## FR5: Manager side: can add and view machines and jobs
### Adding Jobs and Machine scheduling
#### Windows CMD

Schedule a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"machineId\":\"machine5\"}" http://localhost:8787/machinescheduling
```
Get all scheduledIds of all machines:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/machinescheduling/findAllMachineIds
```
Find a machine from a machineId - also shows all allocated jobs (replace `<<machineId>>` with the returned book key):
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/machinescheduling/findMachine?machineId=%machineId%"
```
Add a new job to a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"jobNumber\":1000,\"jobTimeNeededDays\":5,\"priority\":1,\"machineId\":\"machine1\",\"materialNeeded\":\"wood\",\"materialAmount\":23,\"customerName\":\"JohnSmith\"}" http://localhost:8787/addJobToMachine
```
Find all the jobs of a specific machine via machineId
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findJobsByMachineId?machineId=%machineId%"
```
Find the first scheduled job of a specific machine via machineId
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findCurrentJobByMachineId?machineId=%machineId%"
```


## FR1: Manage Machines Work Schedule (agentic)


## FR2: Customer side: can see progress/scheduling of a job
Find a job by job number:
```shell
set jobNumber=<<jobNumber>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findJobByJobNumber?jobNumber=%jobNumber%"
```
```shell
curl "http://localhost:8787/addJobToMachine/findJobInfoByJobNumber?jobNumber=%jobNumber%"
```
Find all jobs from a customer name:
```shell
set customerName=<<customerName>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findAllCustomerJobsByCustomerName?customerName=%customerName%"
```



## FR6: Handling newly submitted jobs (real-time) part 2
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
c:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic jobAddedToMachines --from-beginning
```

# Workforce MS 

### Employee Management APIs

#### 1. Add New Employee
```bash
curl -X POST "http://localhost:8080/api/workforce/employees" -H "Content-Type: application/json" -d "{\"name\":\"John Smith\",\"pay\":28.0,\"skill\":\"Welding,Metal Cutting,Assembly\",\"phoneNumber\":\"555-0101\",\"salary\":4500.0,\"managementArea\":\"Production Floor A\",\"managerName\":\"Manager Anderson\",\"manager\":false}"
```

**Response:**
```json
{
  "success": true,
  "message": "Employee added successfully",
  "employeeId": 1,
  "employee": {
    "id": 1,
    "name": "John Smith",
    "pay": 28.0,
    "skill": "Welding,Metal Cutting,Assembly"
  }
}
```

#### 2. Get All Employees
```bash
curl -X GET "http://localhost:8080/api/workforce/employees"
```

**Response:**
```json
{
  "success": true,
  "count": 2,
  "employees": [
    {
      "id": 1,
      "name": "John Smith",
      "pay": 28.0,
      "skill": "Welding,Metal Cutting,Assembly",
      "phoneNumber": "555-0101",
      "manager": false
    }
  ]
}
```

#### 3. Delete Employee (with all timesheets)
```bash
curl -X DELETE "http://localhost:8080/api/workforce/employees/1"
```

**Response:**
```json
{
  "success": true,
  "message": "Employee and all associated timesheets deleted successfully",
  "deletedEmployee": {
    "employeeId": 1,
    "name": "John Smith"
  },
  "deletedTimesheets": 5
}
```

**Error Response (Employee not found):**
```json
{
  "success": false,
  "error": "Employee not found with ID: 999"
}
```

### Timesheet Management APIs

#### 4. Add New Timesheet for specific Employee
```bash
curl -X POST "http://localhost:8080/api/workforce/timesheets" -H "Content-Type: application/json" -d "{\"employeeId\":1,\"workDate\":\"2025-10-18\",\"hoursWorked\":8.0,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:00:00\",\"jobId\":123}"
```

**Response:**
```json
{
  "success": true,
  "message": "Timesheet recorded successfully",
  "timesheet": {
    "timesheetId": 1,
    "employeeId": 1,
    "workDate": "2025-10-18",
    "hoursWorked": 8.0,
    "salaryPaid": 224.0,
    "status": "EXCEPTION",
    "clockInTime": "2025-10-18T09:00:00",
    "clockOutTime": "2025-10-18T17:00:00"
  }
}
```

#### 5. Get All Timesheets 
```bash
# Get all timesheets
curl -X GET "http://localhost:8080/api/workforce/timesheets"

# Get timesheets for specific employee
curl -X GET "http://localhost:8080/api/workforce/timesheets?employeeId=1"

# Get timesheets by status
curl -X GET "http://localhost:8080/api/workforce/timesheets?status=APPROVED"

# Get timesheets for specific employee with specific status
curl -X GET "http://localhost:8080/api/workforce/timesheets?employeeId=1&status=APPROVED"
```

**Response:**
```json
{
  "success": true,
  "count": 2,
  "timesheets": [
    {
      "timesheetId": 1,
      "employeeId": 1,
      "workDate": "2025-10-18",
      "hoursWorked": 8.0,
      "salaryPaid": 224.0,
      "status": "EXCEPTION",
      "clockInTime": "2025-10-18T09:00:00",
      "clockOutTime": "2025-10-18T17:00:00"
    }
  ]
}
```

#### 6. Approve Timesheet
```bash
curl -X PUT "http://localhost:8080/api/workforce/portal/timesheet/1/approve"
```

**Response:**
```json
{
  "success": true,
  "message": "Timesheet approved successfully",
  "timesheet": {
    "timesheetId": 1,
    "employeeId": 1,
    "status": "APPROVED",
    "workDate": "2025-10-18",
    "hoursWorked": 8.0,
    "salaryPaid": 224.0
  }
}
```

#### 7. Reject Timesheet
```bash
curl -X PUT "http://localhost:8080/api/workforce/portal/timesheet/1/reject"
```

**Response:**
```json
{
  "success": true,
  "message": "Timesheet rejected successfully",
  "timesheet": {
    "timesheetId": 1,
    "employeeId": 1,
    "status": "REJECTED",
    "workDate": "2025-10-18",
    "hoursWorked": 8.0,
    "salaryPaid": 224.0
  }
}
```

### Employee Portal APIs

#### 8. Get Employee Working Hours
```bash
# Get total working hours for employee
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/working-hours"

# Get working hours with date range
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/working-hours?startDate=2025-10-01&endDate=2025-10-31"
```

#### 9. Get Current Salary 
```bash
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/current-salary"
```

#### 11. Get Payslip Summary (todo)
```bash
# Get all payslips
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary"

# Get payslips for specific year and month
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary?year=2025&month=10"
```

## Manager Portal APIs

### Update Clock-In/Out for specific employee
```bash
curl -X POST "http://localhost:8080/api/workforce/portal/employee/1/clock-in-out" -H "Content-Type: application/json" -d "{\"workDate\":\"2025-10-18\",\"hoursWorked\":8.0,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:00:00\"}"
```
**Response:**
```json
{
  "success": true,
  "message": "Clock-in and clock-out recorded successfully",
  "timesheet": {
    "timesheetId": 2,
    "employeeId": 1,
    "workDate": "2025-10-18",
    "hoursWorked": 8.0,
    "salaryPaid": 224.0,
    "status": "EXCEPTION",
    "clockInTime": "2025-10-18T09:00:00",
    "clockOutTime": "2025-10-18T17:00:00"
  }
}
```

#### 12. Auto-generate Shift Plan (agentic)
#### 13. Update Shift Plan
#### 14. Notify Employee 
#### 25. Get Alternative Employees 


### Trouble Shooting
If you cannot start Kafka, try to clean up data in the Kafka topics to start over.
For this purpose, in Linux/MacOS, delete the folders `/tmp/zookeeper`, `/tmp/kafka-logs`
and `/tmp/kafka-streams` (if any). In Windows, delete the folders `C:\tmp\zookeeper`,
`C:\tmp\kafka-logs` and `C:\kafka\kafka-streams` (if any).

