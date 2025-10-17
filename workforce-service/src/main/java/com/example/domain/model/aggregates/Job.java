package com.example.domain.model.aggregates;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Job entity class representing a scheduled job in the system.
 * <p>
 * aggregate root：Job
 * used to manage job-related business operations and maintain consistency within the aggregate.
 * </p>
 */
@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "customer")
    private String customer;

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status")
    private String status;

    @Column(name = "submit_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "title")
    private String title;

    @Column(name = "material_requirement")
    private Integer materialRequirement;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "queue_order_number")
    private Integer queueOrderNumber;

    // 构造方法、getter、setter
    public Job() {}

    public Long getJobId() {
        return jobId; }

    public void setJobId(Long jobId) {
        this.jobId = jobId; }

    public String getCustomer() {
        return customer; }

    public void setCustomer(String customer) {
        this.customer = customer; }

    public Date getDueDate() {
        return dueDate; }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate; }

    public Integer getPriority() {
        return priority; }

    public void setPriority(Integer priority) {
        this.priority = priority; }

    public String getStatus() {
        return status; }

    public void setStatus(String status) {
        this.status = status; }

    public Date getDueDate() {
        return dueDate; }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate; }

    public String getTitle() {
        return title; }

    public void setTitle(String title) {
        this.title = title; }

    public Integer getMaterialRequirement() {
        return materialRequirement; }

    public void setMaterialRequirement(Integer materialRequirement) {
        this.materialRequirement = materialRequirement; }

    public Date getStartDate() {
        return startDate; }

    public void setStartDate(Date startDate) {
        this.startDate = startDate; }

    public Date getEndDate() {
        return endDate; }

    public void setEndDate(Date endDate) {
        this.endDate = endDate; }

    public Integer getQueueOrderNumber() {
        return queueOrderNumber; }

    public void setQueueOrderNumber(Integer queueOrderNumber) {
        this.queueOrderNumber = queueOrderNumber; }

}
