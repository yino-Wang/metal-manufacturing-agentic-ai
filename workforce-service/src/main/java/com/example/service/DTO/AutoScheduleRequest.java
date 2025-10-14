package com.example.service.DTO;

import java.util.Date;

//shiftshcedule request dto

public class AutoScheduleRequest {
    private Date startDate;
    private Date endDate;
    private Long jobId;
    private int requiredEmployees;
    private String shiftType;

    public Date getStartDate() {
        return startDate; }

    public void setStartDate(Date startDate) {
        this.startDate = startDate; }

    public Date getEndDate() {
        return endDate; }

    public void setEndDate(Date endDate) {
        this.endDate = endDate; }

    public Long getJobId() {
        return jobId; }

    public void setJobId(Long jobId) {
        this.jobId = jobId; }

    public int getRequiredEmployees() {
        return requiredEmployees; }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees; }

    public String getShiftType() {
        return shiftType; }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType; }
}