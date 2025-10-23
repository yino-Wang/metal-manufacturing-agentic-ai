# CSCI318-Microservice-System-for-Metal-System

### Four Microservices in Metal Manufacturing System
- **Business MS** 
- **Inventory MS** 
- **Maintenance MS** 
- **Workforce MS** 

# Configuration
This project uses LangChain4j to provide agentic AI capabilities.
To use LangChain4j, a Gemini Api Key is required.
This project is already configured with a key for ease of use.\
However, if you wish to use your own key, update the `langchain4j.google-ai-gemini.chat-model.api-key` property in the microservice's `application.properties` file.
#### Each microservice will specify what set-ups they require (i.e. not all MS's need Kafka)
## Apache Kafka Set-up
This Spring Boot project uses Apache Kafka as a messaging platform.
To run this project, you need to set up Kafka first.

(Linux/MacOS)
Make sure you download kafka version 3.9.- or below, as v4 does not have zookeeper.
Download a **binary package** of Apache Kafka (e.g., `kafka_2.13-3.7.0.tgz`) from
[https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
and unzip it.
In the Terminal, `cd` to the unzip folder, and start Kafka with the following commands in a new Terminal each:
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
and unzip it to a directory: `C:\kafka` is used for the following commands, you will need to change it 
to your directory if you use a different name.

Use the following commands in two different Windows CMD to start Kafka:
```bash
C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
``` 
```bash
C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
```

# Business MS
To run the business microservice you will need to start kafka, as detailed in 'Set-up for the whole application'.
After starting Kafka, run `business-service`'s main class, this main class will create 4 machines and
will continuously assign random jobs to random machines.

### Machine and Job Management APIs
Commands to view, find and add machines and jobs for machines.
#### 1. Get All Machines and their stored information, including all jobs and job schedule
```shell
curl "http://localhost:8787/machinescheduling/findAllMachines"
```

#### 2. View all machineIds of all existing machines
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/machinescheduling/findAllMachineIds
```

#### 3. Find a specific machine from a machineId
Replace `<<machineId>>` with one of the returned machineIds from step 2, or enter it if you know the machineId,
(the four initialised machines in main are machine1, machine2, machine3, machine4 respectively)

(Windows)
```shell
set machineId=<<machineId>>
```
(Linux/MacOS)
```shell
machineId="<<machineId>>"
```
This command works for all operating systems to find pass the parameter:
```shell
curl "http://localhost:8787/machinescheduling/findMachine?machineId=%machineId%"
```

#### 4. Add a new machine
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"machineId\":\"machine5\"}" http://localhost:8787/machinescheduling
```

#### 5. Add a new job to a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"jobNumber\":1000,\"jobTimeNeededDays\":5,\"priority\":1,\"machineId\":\"machine1\",\"materialNeeded\":\"steel\",\"materialAmount\":23,\"customerName\":\"John\"}" http://localhost:8787/addJobToMachine
```

#### 6. Find all the jobs of a specific machine via machineId
Replace `<<machineId>>` with one of the returned machineIds from step 2, or enter it if you know the machineId,
(the four initialised machines in main are machine1, machine2, machine3, machine4 respectively)

machine5 also now exists in the system with one added job

(Windows)
```shell
set machineId=<<machineId>>
```
(Linux/MacOS)
```shell
machineId="<<machineId>>"
```
This command works for all operating systems to find pass the parameter:
```shell
curl "http://localhost:8787/addJobToMachine/findJobsByMachineId?machineId=%machineId%"
```

#### 7. Find a job by job number:
Replace `<<jobNumber>>` with a jobNumber, a number starting from 1

(main adds a new job evey 5 seconds and each new job's jobNumber is increased by 1)

(Windows)
```shell
set jobNumber=<<jobNumber>>
```
(Linux/MacOS)
```shell
jobNumber=<<jobNumber>>
```
This command works for all operating systems to find pass the parameter:
```shell
curl "http://localhost:8787/addJobToMachine/findJobByJobNumber?jobNumber=%jobNumber%"
```

### Customer portal APIs
Commands to view and find jobs associated with a customer
#### 8. Find a job by job number:
Replace `<<jobNumber>>` with a jobNumber, a number starting from 1

(main adds a new job evey 5 seconds and each new job's jobNumber is increased by 1)

In reality the customer should already know their job numbers and should not be allowed to get a list of all job numbers

For this reason, it is assumed the customer already knows the number they want to search for

(Windows)
```shell
set jobNumber=<<jobNumber>>
```
(Linux/MacOS)
```shell
jobNumber=<<jobNumber>>
```
This command works for all operating systems to find pass the parameter:
```shell
curl "http://localhost:8787/addJobToMachine/findJobInfoByJobNumber?jobNumber=%jobNumber%"
```

#### 9. Find all jobs from a customer name:
Replace `<<customerName>>` with a customerName

For security reasons a customer cannot view a list of all customers and is expected to know their name in the system

Available names to search with: Michelle, Randy, Rob, Deb, John

(Windows)
```shell
set customerName=<<customerName>>
```
(Linux/MacOS)
```shell
customerName="<<customerName>>"
```
This command works for all operating systems to find pass the parameter:
```shell
curl "http://localhost:8787/addJobToMachine/findAllCustomerJobsByCustomerName?customerName=%customerName%"
```

### Monitor new job's material needs APIs
Commands to view the needed materials per machine per material type in a 30-second window.
#### 10. The following REST API is provided to query the windowed stream processing results:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/queries/windowedMachinesByAmount
```

#### 11. View Booking Event Stream
After running the `business-service`'s main class, check the Kafka topics with the following command:

(Linux/MacOS)
```shell
./bin/kafka-topics.sh --bootstrap-server=localhost:9092 --list
```
(Windows)
```shell
C:\kafka\bin\windows\kafka-topics.bat --bootstrap-server=localhost:9092 --list
```
You can read data in the `jobAddedToMachines` topic: 

(Linux/MacOS)
```shell
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic jobAddedToMachines --from-beginning
```
(Windows)
```shell
c:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic jobAddedToMachines --from-beginning
```

### Schedule job APIs
This functional requirement does not require the user to use any unique or different commands. 
Every time a job is added (either in the main loop or by a user) the agentic AI scheduler is called
and the new job's assigned machine's schedule is updated and saved. This means that whenever the above
manager APIs are called, the generated schedule can already be viewed (as long as there is at least one job in a machine).

In order to not have to find the relevant command again - the below command will show all machines and their schedules.
Keep running the command every 5 seconds (after a new job is added) to see how the schedule dynamically changes.
#### 12. View machines and their agentic AI generated schedules
```shell
curl "http://localhost:8787/machinescheduling/findAllMachines"
```

# Inventory MS
To run application, first run kafka then open and 
run the business-service [Main.java](business-service/src/main/java/com/example/Main.java).
Then open and run the inventory-service 
[InventoryServiceApplication.java](inventory-service/src/main/java/com/example/InventoryServiceApplication.java).

When you run the inventory-service application, it will automatically load 4 materials into the inventory with 150 units each, as shown below:
```
--------------------------------------------------
[Init] Adding default materials into inventory...
--------------------------------------------------
[Init] Default materials added successfully!
[Inventory] Current materials in stock:
Steel (ID: 1) - 150 units
Aluminium (ID: 2) - 150 units
Copper (ID: 3) - 150 units
Iron (ID: 4) - 150 units
```

### Inventory Management APIs
#### 1. Automatic Restocking of Low Stock Items
Whenever a material’s quantity drops below 100 units,
the system will automatically increase its quantity by +200 units,
then save the new total to the database.

**Example:**

```declarative
Material: "Copper"
Previous quantity: 80
Auto-restock applied → new quantity = 280
```
#### 2. Add a Material
```bash
curl -X POST "http://localhost:8081/api/inventory/add" -H "Content-Type: application/json" -d "{\"name\":\"Zinc\",\"quantity\":150}"
```
**Response:**
```json
{"status":"success",
  "material":{"id":5,"name":"Zinc","quantity":150,"lowStock":false},
  "message":"Material added successfully (auto-restock applies if below 100)."
}
```

#### 3. Update Material Quantity
```bash
curl -X PUT "http://localhost:8081/api/inventory/update/1?quantity=60"
```
**Response:**
```json
{
  "status":"success",
  "material":{"id":1,"name":"Steel","quantity":260,"lowStock":false},
  "message":"Stock updated successfully (auto-restock applies if below 100)."
}
```

#### 4. Get all Materials
```bash
curl -X GET "http://localhost:8081/api/inventory/list"
```
**Response:**
```json
[
  {
    "id":1,
    "name":"Steel",
    "quantity":260,
    "lowStock":false
  },
  {
    "id":2,
    "name":"Aluminium",
    "quantity":150,
    "lowStock":false
  },
  {
    "id":3,
    "name":"Copper",
    "quantity":150,
    "lowStock":false
  },
  {
    "id":4,
    "name":"Iron",
    "quantity":150,
    "lowStock":false
  },
  {
    "id":5,
    "name":"Zinc",
    "quantity":150,
    "lowStock":false
  }
]
```
#### 5. The following REST API is provided to query the windowed stream processing results:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8081/queries/recentMaterialUsage
```
#### 6. View material Event Stream
After running the `business-service`'s and `inventory-service`'s main class, check the Kafka topics with the following command:

(Linux/MacOS)
```shell
./bin/kafka-topics.sh --bootstrap-server=localhost:9092 --list
```
(Windows)
```shell
C:\kafka\bin\windows\kafka-topics.bat --bootstrap-server=localhost:9092 --list
```
You can read data in the `materialNeeded` topic:

(Linux/MacOS)
```shell
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic materialNeeded --from-beginning
```
(Windows)
```shell
c:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic materialNeeded --from-beginning
```


# Maintenance MS
Run `MaintenanceReportServiceApplication.java` to start the Maintenance MS.\
The microservice employs a persistent database (H2) to store maintenance reports for machines, and should be prepopulated with 4 machines and 4 reports.

### Report Management APIs
Commands to view, add, update, and delete maintenance reports for machines.
#### 1. Get All Machines and Corresponding Reports
```bash
curl -X GET "http://localhost:8084/maintenance/machines"
```
#### Response:
```json
[
	{
		"machineId": "machine1",
		"reports": [
			{
				"reportId": "11",
				"reportDate": "2025-10-03",
				"machineId": "machine1",
				"issue": "Poor Surface Finish",
				"solution": "Replace Loose Chuck"
			}
		]
	},
	{
		"machineId": "machine2",
		"reports": [
			{
				"reportId": "21",
				"reportDate": "2025-10-14",
				"machineId": "machine2",
				"issue": "Excessive Tool Wear",
				"solution": "Reduce Cutting Speed"
			}
		]
	},
	{
		"machineId": "machine3",
		"reports": [
			{
				"reportId": "31",
				"reportDate": "2025-09-28",
				"machineId": "machine3",
				"issue": "Edges burred",
				"solution": "Use sharper tool"
			}
		]
	},
	{
		"machineId": "machine4",
		"reports": [
			{
				"reportId": "41",
				"reportDate": "2025-09-29",
				"machineId": "machine4",
				"issue": "Poor surface finish",
				"solution": "Fix coolant pressure"
			}
		]
	}
]
```
#### 2. Add New Maintenance Report
```bash
curl -X POST "http://localhost:8084/maintenance/reports" -H "Content-Type: application/json" -d "{\"reportId\":\"12\",\"reportDate\":\"2025-10-23\",\"machineId\":\"machine1\",\"issue\":\"Machine Chatter\",\"solution\":\"Reduce Tool Overhang\"}"
```
#### 3. View Specific Machine's Reports
```bash
curl -X GET "http://localhost:8084/maintenance/machines/machine1
```
#### Response:
```json
[
    {
        "reportId": "11",
        "reportDate": "2025-10-03",
        "machineId": "machine1",
        "issue": "Poor Surface Finish",
        "solution": "Replace Loose Chuck"
    },
    {
        "reportId": "12",
        "reportDate": "2025-10-23",
        "machineId": "machine1",
        "issue": "Machine Chatter",
        "solution": "Reduce Tool Overhang"
    }
]
```
#### 4. Update Maintenance Report
```bash
curl -X PATCH "http://localhost:8084/maintenance/reports/12" -H "Content-Type: application/json" -d "{\"reportDate\":\"2025-10-24\"}"
```
#### 5. View Specific Maintenance Report
```bash
curl -X GET "http://localhost:8084/maintenance/reports/12"
```
#### Response:
```json
{
    "reportId": "12",
    "reportDate": "2025-10-24",
    "machineId": "machine1",
    "issue": "Machine Chatter",
    "solution": "Reduce Tool Overhang"
}
```
#### 6. Delete Maintenance Report
```bash
curl -X DELETE "http://localhost:8084/maintenance/reports/12"
```
### Agentic Support APIs
Microservice employs an agentic support system to assist users in troubleshooting machine issues based on historical maintenance reports.
Following are example commands to interact with the agentic support system.
#### 1. Enquire Agent about Machine Issue
Post request includes field 'userMessage' which includes the content of the user's enquiry about a machine issue.
```bash
curl -X POST "http://localhost:8084/maintenance/support" -H "Content-Type: application/json" -d "{\"sessionId\":\"session-123\",\"userMessage\":\"Machine 4 is producing poor surface finish\"}"
```
#### 2. Continue Support Conversation until Solution
Provide subsequent requested information in the 'userMessage' field until a solution is suggested e.g.
```bash
curl -X POST "http://localhost:8084/maintenance/support" -H "Content-Type: application/json" -d "{\"sessionId\":\"session-123\",\"userMessage\":\"No loud sounds and no warning on the control panel\"}"
```
The provided solution will contain information of the report it is drawn from and a summary of the suggested solution.
#### Example Response:
```
Machine Id: machine4 | Issue: Poor surface finish | Solution: Fix coolant pressure

The provided solution suggests fixing the coolant pressure. 
This could help because inadequate coolant pressure can lead to increased friction and heat between the cutting tool and the workpiece, resulting in a poor surface finish. 
Ensuring proper coolant pressure will help to dissipate heat, lubricate the cutting process, and flush away chips, all of which contribute to a better surface finish.
```



# Workforce MS 

### Employee Management APIs

#### 1. Add New Employee
Windows
```bash
curl -X POST "http://localhost:8080/api/workforce/employees" -H "Content-Type: application/json" -d "{\"name\":\"Alice Wu\",\"pay\":28.0,\"skill\":\"Normal\",\"phoneNumber\":\"555-0101\",\"salary\":4500.0,\"managementArea\":\"None\",\"managerName\":\"Manager Anderson\",\"manager\":false}"
```
Mac/Linux
```bash
curl -X POST "http://localhost:8080/api/workforce/employees" \ -H "Content-Type: application/json" \ -d '{"name":"Alice Wu","pay":28.0,"skill":"Normal","phoneNumber":"555-0101","salary":4500.0,"managementArea":"None","managerName":"Manager Anderson","manager":false}'
```

**Response:**
```json
{
  "success": true,
  "message": "Employee added successfully",
  "employeeId": 1,
  "employee": {
    "id": 1,
    "name": "Alice Wu",
    "pay": 28.0,
    "skill": "Normal"
  }
}
```

#### 2. Get All Employees
Windows
```bash
curl -X GET "http://localhost:8080/api/workforce/employees"
```
Mac/Linux
```bash
curl -H "Accept: application/json" 'http://localhost:8080/api/workforce/employees'
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
Windows
```bash
curl -X DELETE "http://localhost:8080/api/workforce/employees/1"
```
Mac/Linux
```bash
curl -X DELETE 'http://localhost:8080/api/workforce/employees/1' -H 'Accept: application/json'
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
Windows
```bash
curl -X POST "http://localhost:8080/api/workforce/timesheets" -H "Content-Type: application/json" -d "{\"employeeId\":5,\"workDate\":\"2025-10-18\",\"hoursWorked\":8.0,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:00:00\",\"jobId\":5}"
```
Mac/Linux
```bash
curl -X POST 'http://localhost:8080/api/workforce/timesheets' -H 'Content-Type: application/json' -d '{"employeeId":5,"workDate":"2025-10-18","hoursWorked":8.0,"clockInTime":"2025-10-18T09:00:00","clockOutTime":"2025-10-18T17:00:00","jobId":5}'
```
**Response:**
```json
{
  "success": true,
  "message": "Timesheet recorded successfully",
  "timesheet": {
    "timesheetId": 5,
    "employeeId": 5,
    "workDate": "2025-10-18",
    "hoursWorked": 8.0,
    "salaryPaid": 224.0,
    "status": "EXCEPTION",
    "clockInTime": "2025-10-18T09:00:00",
    "clockOutTime": "2025-10-18T17:00:00",
    "jobId": 5
  }
}
```

#### 5. Get All Timesheets 
Windows
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
Mac/Linux
```bash
# Get all timesheets
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/timesheets'

# Get timesheets for specific employee
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/timesheets?employeeId=1'

# Get timesheets by status
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/timesheets?status=APPROVED'

# Get timesheets for specific employee with specific status
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/timesheets?employeeId=1&status=APPROVED'
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
Windows
```bash
curl -X PUT "http://localhost:8080/api/workforce/portal/timesheet/1/approve"
```
Mac/Linux
```bash
curl -X PUT 'http://localhost:8080/api/workforce/portal/timesheet/1/approve' -H 'Accept: application/json'
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
Windows
```bash
curl -X PUT "http://localhost:8080/api/workforce/portal/timesheet/1/reject"
```
Mac/Linux
```bash
curl -X PUT 'http://localhost:8080/api/workforce/portal/timesheet/1/reject' -H 'Accept: application/json'
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
Windows
```bash
# Get total working hours for employee
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/working-hours"

# Get working hours with date range
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/working-hours?startDate=2025-10-01&endDate=2025-10-31"
```
Mac/Linux
```bash
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/portal/employee/1/working-hours'

# Get working hours with date range
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/portal/employee/1/working-hours?startDate=2025-10-01&endDate=2025-10-31'
```

#### 9. Get Current Salary 
Windows
```bash
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/current-salary"
```
Mac/Linux
```bash
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/portal/employee/1/current-salary'
```
#### 11. Get Payslip Summary 
Windows
```bash
# Get all payslips
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary"

# Get payslips for specific year and month
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary?year=2025&month=10"
```
Mac/Linux
```bash
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/portal/employee/1/payslip-summary'

curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/portal/employee/1/payslip-summary?year=2025&month=10'
```
## Manager Portal APIs

### Update Clock-In/Out for specific employee
Windows
```bash
curl -X POST "http://localhost:8080/api/workforce/portal/employee/1/clock-in-out" -H "Content-Type: application/json" -d "{\"workDate\":\"2025-10-18\",\"hoursWorked\":8.0,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:00:00\",\"jobId\":1}"
```
Mac/Linux
```bash
curl -X POST 'http://localhost:8080/api/workforce/portal/employee/1/clock-in-out' \ 
  -H 'Content-Type: application/json' \ 
  -d '{"workDate":"2025-10-18","hoursWorked":8.0,"clockInTime":"2025-10-18T09:00:00","clockOutTime":"2025-10-18T17:00:00","jobId":1}'
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

### 12. Auto-generate Shift Plan using data from Business MS (agentic) and notify employees
Windows
```bash
curl -X POST "http://localhost:8080/api/shift-planner/create-from-api?machineId=machine2"
```
Mac/Linux
```bash
curl -X POST 'http://localhost:8080/api/shift-planner/create-from-api?machineId=machine2' \ 
  -H 'Accept: application/json'
```
#### 12(1) . Fetch machine schedule from Business MS
Windows
```bash
curl "http://localhost:8080/api/shift-planner/api-schedule"
```
Mac/Linux
```bash
curl -H 'Accept: application/json' 'http://localhost:8080/api/shift-planner/api-schedule'
```
### 13. Update Shift Plan
Windows
```bash
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1" -H "Content-Type: application/json" -d "{\"employeeId\":1,\"shiftDate\":\"2025-10-22\",\"startTime\":\"2025-10-22\",\"endTime\":\"2025-10-23\",\"status\":\"BUSY\",\"version\":2,\"jobId\":10,\"requiredEmployees\":1}"
```
Mac/Linux
```bash
curl -X PUT 'http://localhost:8080/api/workforce/manager/portal/shift-plan/1' \
  -H 'Content-Type: application/json' \
  -d '{"employeeId":1,"shiftDate":"2025-10-22","startTime":"2025-10-22","endTime":"2025-10-23","status":"BUSY","version":2,"jobId":10,"requiredEmployees":1}'
```
**responseL**
```json
{
  "success": true,
  "message": "Shift plan updated successfully",
  "shiftPlan": {
    "shiftPlanId": 1,
    "employeeId": 1,
    "shiftDate": "2025-10-22",
    "startTime": "2025-10-23",
    "endTime": "2025-10-24",
    "status": "BUSY",
    "version": 2,
    "jobId": 9
  }
}
```

### 14. Get all Shift Plans
Windows
```bash
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans"
```
Mac/Linux
```bash
curl -H 'Accept: application/json' 'http://localhost:8080/api/workforce/manager/portal/shift-plans'
```




### Trouble Shooting
If you cannot start Kafka, try to clean up data in the Kafka topics to start over.
For this purpose, in Linux/MacOS, delete the folders `/tmp/zookeeper`, `/tmp/kafka-logs`
and `/tmp/kafka-streams` (if any). In Windows, delete the folders `C:\tmp\zookeeper`,
`C:\tmp\kafka-logs` and `C:\kafka\kafka-streams` (if any).

