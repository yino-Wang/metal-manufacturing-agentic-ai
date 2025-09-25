package com.example.domain.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payslip")
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payslipId;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "gross_pay")
    private Float grossPay;

    @Column(name = "pay")
    private Float pay;

    @Column(name = "tax")
    private Float tax;

    @Column(name = "super")
    private Float superAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Employee employee;

    // 构造方法、getter、setter
    public Payslip() {}

    public Long getPayslipId() { return payslipId; }
    public void setPayslipId(Long payslipId) { this.payslipId = payslipId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Float getGrossPay() { return grossPay; }
    public void setGrossPay(Float grossPay) { this.grossPay = grossPay; }
    public Float getPay() { return pay; }
    public void setPay(Float pay) { this.pay = pay; }
    public Float getTax() { return tax; }
    public void setTax(Float tax) { this.tax = tax; }
    public Float getSuperAmount() { return superAmount; }
    public void setSuperAmount(Float superAmount) { this.superAmount = superAmount; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}

