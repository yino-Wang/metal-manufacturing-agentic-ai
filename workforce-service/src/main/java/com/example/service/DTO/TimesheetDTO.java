package com.example.service.DTO;

public class TimesheetDTO {
    private Long id;
    private Integer employeeId;
    private String workDate; // Use String to simplify date handling in DTO
    private Float hoursWorked;
    private Float salaryPaid;

    public TimesheetDTO() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public Float getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Float hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Float getSalaryPaid() {
        return salaryPaid;
    }

    public void setSalaryPaid(Float salaryPaid) {
        this.salaryPaid = salaryPaid;
    }

    public Float getHoursWorkedPaid() {
        return hoursWorked * salaryPaid;
    }
}
