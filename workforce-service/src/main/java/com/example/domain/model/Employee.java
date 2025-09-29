package com.example.domain.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Employee entity class representing an employee in the system.
 */
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "skill")
    private String skill;

    @Column(name = "salary")
    private Float salary;

    @Column(name = "pay")
    private Float pay;

    @Column(name = "start_date_payslip")
    @Temporal(TemporalType.DATE)
    private Date startDatePayslip;

    @Column(name = "end_date_payslip")
    @Temporal(TemporalType.DATE)
    private Date endDatePayslip;

    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "manager")
    private Boolean manager;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "management_area")
    private String managementArea;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "scheduled_jobs")
    private Integer scheduledJobs;

    /**
     * Default constructor.
     */
    public Employee() {}

    /**
     * Parameterized constructor to create an Employee instance.
     *
     * @param employeeId          the unique identifier of the employee
     * @param name             the name of the employee
     * @param phoneNumber      the phone number of the employee
     * @param skill            the skill of the employee
     * @param salary           the salary of the employee
     * @param pay              the pay of the employee
     * @param startDatePayslip the start date of the payslip period
     * @param endDatePayslip   the end date of the payslip period
     * @param scheduleId       the schedule identifier of the employee
     * @param manager          whether the employee is a manager
     * @param managerName      the name of the manager
     * @param managementArea   the area managed by the manager
     * @param user             the user associated with the employee
     * @param scheduledJobs     the number of scheduled jobs for the employee
     */
    public Employee(Long employeeId, String name, String phoneNumber, Float salary, Float pay, String skill,
                    Date startDatePayslip, Date endDatePayslip, Integer scheduleId, Boolean manager,
                    String managerName, String managementArea, User user, Integer scheduledJobs) {
        this.employeeId = employeeId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pay = pay;
        this.salary = salary;
        this.skill = skill;
        this.startDatePayslip = startDatePayslip;
        this.endDatePayslip = endDatePayslip;
        this.scheduleId = scheduleId;
        this.manager = manager;
        this.managerName = managerName;
        this.managementArea = managementArea;
        this.user = user;
        this.scheduledJobs = scheduledJobs;
    }

    // Getter and setter methods

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Float getPay() {
        return pay;
    }

    public void setPay(Float pay) {
        this.pay = pay;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Date getStartDatePayslip() {
        return startDatePayslip;
    }

    public void setStartDatePayslip(Date startDatePayslip) {
        this.startDatePayslip = startDatePayslip;
    }

    public Date getEndDatePayslip() {
        return endDatePayslip;
    }

    public void setEndDatePayslip(Date endDatePayslip) {
        this.endDatePayslip = endDatePayslip;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Boolean getManager() {
        return manager;
    }

    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagementArea() {
        return managementArea;
    }

    public void setManagementArea(String managementArea) {
        this.managementArea = managementArea;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScheduledJobs() {
        return scheduledJobs;
    }

    public void setScheduledJobs(Integer scheduledJobs) {
        this.scheduledJobs = scheduledJobs;
    }
}
