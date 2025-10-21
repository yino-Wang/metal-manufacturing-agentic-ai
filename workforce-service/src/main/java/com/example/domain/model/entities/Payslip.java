package com.example.domain.model.entities;

import com.example.domain.model.aggregates.Employee;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payslip")
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payslipId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "total_salary")
    private Float totalSalary;

    @Column(name = "this_pay")
    private Float thisPay;

    // 构造方法、getter、setter
    public Payslip() {}

    public Payslip(Long payslipId, Date startDate, Date endDate, Float totalSalary, Float thisPay, Long employeeId) {
        this.payslipId = payslipId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSalary = totalSalary;
        this.thisPay = thisPay;
        this.employeeId = employeeId;
    }

    public Long getPayslipId() {
        return payslipId; }

    public void setPayslipId(Long payslipId) {
        this.payslipId = payslipId; }

    public Date getStartDate() {
        return startDate; }

    public void setStartDate(Date startDate) {
        this.startDate = startDate; }

    public Date getEndDate() {
        return endDate; }

    public void setEndDate(Date endDate) {
        this.endDate = endDate; }

    public Float getTotalSalary() {
        return totalSalary; }

    public void setTotalSalary(Float totalSalary) {
        this.totalSalary = totalSalary; }

    public Float getThisPay() {
        return thisPay; }

    public void setThisPay(Float thisPay) {
        this.thisPay = thisPay; }

    public Long getEmployeeId() {
        return employeeId; }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId; }


}

