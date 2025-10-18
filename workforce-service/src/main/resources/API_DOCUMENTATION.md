# WorkforceController API Documentation

## Overview
The WorkforceController has been completely updated and improved with proper REST API design, comprehensive error handling, and new functionality.

## 🔧 Issues Fixed in RecordTimesheet Code

### Previous Issues:
1. **Poor API Design**: Used `@RequestParam` for complex data types (Date, LocalDateTime)
2. **Inconsistent Naming**: `/clock-in-out` endpoint was unclear
3. **Missing DTOs**: No proper request/response objects
4. **Date Parsing Issues**: Problems with URL parameter date formats
5. **Poor Error Handling**: Basic error responses
6. **Repository Method Errors**: Used incorrect `findByEmployeeId()` instead of `findByEmployee_EmployeeId()`

### Solutions Implemented:
✅ **Proper DTOs**: Created `RecordTimesheetRequest` and `AddEmployeeRequest`
✅ **RESTful Design**: Changed to `@RequestBody` with JSON
✅ **Comprehensive Validation**: Input validation with detailed error messages
✅ **Better Error Handling**: Structured error responses with HTTP status codes
✅ **Fixed Repository Calls**: Used correct method names
✅ **Backward Compatibility**: Kept legacy endpoint as deprecated

## 🆕 New API Endpoints

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

#### 4. Add New Timesheet (Improved)
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

#### 5. Get All Timesheets (with filtering)
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

#### 10. Get Schedule Summary
```bash
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/schedule-summary"
```

#### 11. Get Payslip Summary
```bash
# Get all payslips
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary"

# Get payslips for specific year and month
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/payslip-summary?year=2025&month=10"
```

## 🔄 Updated Existing Endpoints

### Legacy Clock-In/Out (Deprecated but supported)
```bash
curl -X POST "http://localhost:8080/api/workforce/portal/employee/1/clock-in-out" -H "Content-Type: application/json" -d "{\"workDate\":\"2025-10-18\",\"hoursWorked\":8.0,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:00:00\"}"
```

### Manager Portal APIs (Updated)

#### 12. Auto-generate Shift Plan (Enhanced)
```bash
curl -X POST "http://localhost:8080/api/workforce/manager/portal/auto-schedule" -H "Content-Type: application/json" -d "{\"startDate\":\"2025-10-20\",\"endDate\":\"2025-10-27\",\"jobId\":123,\"requiredEmployees\":3,\"priority\":2}"
```

**Success Response:**
```json
{
  "success": true,
  "message": "Shift plan generated successfully",
  "shiftPlans": [
    {
      "shiftPlanId": 1,
      "employeeId": 1,
      "shiftDate": "2025-10-20",
      "status": "PENDING_APPROVAL"
    }
  ],
  "alternatives": [],
  "note": "Some shifts could not be filled, alternatives provided"
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "Failed to generate shift plan: No available employees",
  "alternatives": [
    {
      "employeeId": 5,
      "name": "Alternative Employee",
      "skill": "Similar Skills"
    }
  ],
  "message": "No available employees found, please consider the alternatives provided"
}
```

#### 13. Update Shift Plan (Enhanced)
```bash
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1" -H "Content-Type: application/json" -d "{\"employeeId\":2,\"shiftDate\":\"2025-10-21\",\"requiredEmployees\":2,\"jobId\":123,\"status\":\"PENDING_APPROVAL\"}"
```

**Response:**
```json
{
  "success": true,
  "message": "Shift plan updated successfully",
  "shiftPlan": {
    "shiftPlanId": 1,
    "employeeId": 2,
    "shiftDate": "2025-10-21",
    "status": "PENDING_APPROVAL",
    "version": 2,
    "jobId": 123,
    "requiredEmployees": 2
  }
}
```

#### 14. Approve Shift Plan (Enhanced)
```bash
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/approve"
```

**Response:**
```json
{
  "success": true,
  "message": "Shift plan approved successfully",
  "shiftPlan": {
    "shiftPlanId": 1,
    "employeeId": 2,
    "shiftDate": "2025-10-21",
    "status": "APPROVED",
    "version": 3,
    "approvedAt": "2025-10-18T16:30:00.000+00:00"
  },
  "notification": "Employee has been notified automatically"
}
```

#### 15. Reject Shift Plan (New)
```bash
# Reject without reason
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/reject"

# Reject with reason
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/reject" -H "Content-Type: application/json" -d "{\"reason\":\"Employee unavailable due to illness\"}"
```

**Response:**
```json
{
  "success": true,
  "message": "Shift plan rejected successfully",
  "shiftPlan": {
    "shiftPlanId": 1,
    "employeeId": 2,
    "shiftDate": "2025-10-21",
    "status": "REJECTED",
    "rejectionReason": "Employee unavailable due to illness",
    "rejectedAt": "2025-10-18T16:30:00.000+00:00"
  }
}
```

#### 16. Get All Shift Plans with Filtering and Pagination (New)
```bash
# Get all shift plans
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans"

# Get shift plans for specific employee
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans?employeeId=1"

# Get shift plans by status
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans?status=APPROVED"

# Get shift plans with pagination
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans?page=0&size=5"

# Combined filters
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans?employeeId=1&status=PENDING_APPROVAL&page=0&size=10"
```

**Response:**
```json
{
  "success": true,
  "shiftPlans": [
    {
      "shiftPlanId": 1,
      "employeeId": 1,
      "shiftDate": "2025-10-20",
      "status": "PENDING_APPROVAL",
      "jobId": 123,
      "requiredEmployees": 2,
      "version": 1
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 15,
    "totalPages": 2
  }
}
```

#### 17. Validate Shift Plan Compliance (Legacy)
```bash
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/validate"
```

#### 18. Get Shift Plan Version History (Legacy)
```bash
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/versions"
```

#### 19. Notify Employee (Legacy)
```bash
curl -X POST "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/notify"
```

#### 20. Get Alternative Employees (Legacy)
```bash
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/alternatives?skill=Welding&maxCost=30.0"
```

## ✨ Key Improvements

### 1. **Comprehensive Validation**
- Required field validation
- Employee existence verification
- Data type and range validation
- Detailed error messages

### 2. **Structured Responses**
- Consistent JSON response format
- Success/error indicators
- Detailed error messages
- Proper HTTP status codes

### 3. **Better Error Handling**
- Different error types (400, 404, 500)
- Meaningful error messages
- Exception catching and logging

### 4. **RESTful API Design**
- Proper HTTP methods (GET, POST, PUT, DELETE)
- Resource-based URLs
- JSON request/response bodies
- Consistent naming conventions

### 5. **Input Validation**
- Employee ID existence check
- Date format validation
- Hours worked range validation
- Required field verification

## 🧪 Testing Results

All tests are passing successfully:
- ✅ Employee creation and retrieval
- ✅ Timesheet recording and management
- ✅ Salary calculation (8 hours × $28/hour = $224)
- ✅ Error handling for invalid inputs
- ✅ Kafka integration working properly

## 📝 Complete Usage Examples

### Typical Workflow - Create Employee and Record Timesheet:

```bash
# Step 1: Add a new employee
curl -X POST "http://localhost:8080/api/workforce/employees" -H "Content-Type: application/json" -d "{\"name\":\"John Smith\",\"pay\":28.0,\"skill\":\"Welding,Metal Cutting,Assembly\",\"phoneNumber\":\"555-0101\",\"salary\":4500.0,\"managementArea\":\"Production Floor A\",\"managerName\":\"Manager Anderson\",\"manager\":false}"

# Step 2: Record timesheet for the employee
curl -X POST "http://localhost:8080/api/workforce/timesheets" -H "Content-Type: application/json" -d "{\"employeeId\":1,\"workDate\":\"2025-10-18\",\"hoursWorked\":8.5,\"clockInTime\":\"2025-10-18T09:00:00\",\"clockOutTime\":\"2025-10-18T17:30:00\"}"

# Step 3: Get all timesheets for the employee
curl -X GET "http://localhost:8080/api/workforce/timesheets?employeeId=1"

# Step 4: Approve the timesheet (if needed)
curl -X PUT "http://localhost:8080/api/workforce/portal/timesheet/1/approve"

# Step 5: Check employee's working hours summary
curl -X GET "http://localhost:8080/api/workforce/portal/employee/1/working-hours"
```

### Batch Operations:

```bash
# Get all employees and timesheets for reporting
curl -X GET "http://localhost:8080/api/workforce/employees"
curl -X GET "http://localhost:8080/api/workforce/timesheets"

# Filter operations
curl -X GET "http://localhost:8080/api/workforce/timesheets?status=EXCEPTION"
curl -X GET "http://localhost:8080/api/workforce/timesheets?status=APPROVED"
```

### Manager Portal Workflow:

```bash
# Step 1: Generate shift plan
curl -X POST "http://localhost:8080/api/workforce/manager/portal/auto-schedule" -H "Content-Type: application/json" -d "{\"startDate\":\"2025-10-20\",\"endDate\":\"2025-10-27\",\"jobId\":123,\"requiredEmployees\":3,\"priority\":2}"

# Step 2: View all shift plans
curl -X GET "http://localhost:8080/api/workforce/manager/portal/shift-plans"

# Step 3: Update a shift plan if needed
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1" -H "Content-Type: application/json" -d "{\"employeeId\":2,\"shiftDate\":\"2025-10-21\",\"requiredEmployees\":2,\"jobId\":123,\"status\":\"PENDING_APPROVAL\"}"

# Step 4: Approve the shift plan
curl -X PUT "http://localhost:8080/api/workforce/manager/portal/shift-plan/1/approve"
```

## 🔗 Quick Reference

| Operation | Method | Endpoint | Description |
|-----------|--------|----------|-------------|
| **Employees** | | | |
| Add Employee | POST | `/api/workforce/employees` | Create new employee |
| Get All Employees | GET | `/api/workforce/employees` | Retrieve all employees |
| Delete Employee | DELETE | `/api/workforce/employees/{id}` | Delete employee and all timesheets |
| **Timesheets** | | | |
| Add Timesheet | POST | `/api/workforce/timesheets` | Record new timesheet |
| Get Timesheets | GET | `/api/workforce/timesheets` | Get timesheets (with filters) |
| Approve Timesheet | PUT | `/portal/timesheet/{id}/approve` | Approve timesheet |
| Reject Timesheet | PUT | `/portal/timesheet/{id}/reject` | Reject timesheet |
| **Portal** | | | |
| Working Hours | GET | `/portal/employee/{id}/working-hours` | Get employee hours |
| Current Salary | GET | `/portal/employee/{id}/current-salary` | Get salary info |
| Schedule Summary | GET | `/portal/employee/{id}/schedule-summary` | Get schedule info |
| **Manager** | | | |
| Auto Schedule | POST | `/manager/portal/auto-schedule` | Generate shift plan |
| Get Shift Plans | GET | `/manager/portal/shift-plans` | Get all shift plans with filters |
| Update Shift | PUT | `/manager/portal/shift-plan/{id}` | Update shift plan |
| Approve Shift | PUT | `/manager/portal/shift-plan/{id}/approve` | Approve shift plan |
| Reject Shift | PUT | `/manager/portal/shift-plan/{id}/reject` | Reject shift plan |

The WorkforceController is now production-ready with proper API design, comprehensive error handling, and full CRUD operations for both employees and timesheets!
