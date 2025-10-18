package com.example.shared;

import java.time.LocalDate;
import java.util.Objects;

public class JobDto {
    private Long jobId;
    private LocalDate dueDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String materialNeeded;
    private Integer materialAmount;
    private Integer jobTimeNeededDays;
    private Integer priority;
    private String title;


    public JobDto() {}

    public JobDto(Long jobId, LocalDate dueDate, LocalDate startDate, LocalDate endDate,
                  String materialNeeded, Integer materialAmount,
                  Integer jobTimeNeededDays, Integer priority, String title) {
        this.jobId = jobId;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.materialNeeded = materialNeeded;
        this.materialAmount = materialAmount;
        this.jobTimeNeededDays = jobTimeNeededDays;
        this.priority = priority;
        this.title = title;
    }

    // Getters and Setters
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getMaterialNeeded() {
        return materialNeeded;
    }

    public void setMaterialNeeded(String materialNeeded) {
        this.materialNeeded = materialNeeded;
    }

    public Integer getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(Integer materialAmount) {
        this.materialAmount = materialAmount;
    }


    public Integer getJobTimeNeededDays() {
        return jobTimeNeededDays;
    }

    public void setJobTimeNeededDays(Integer jobTimeNeededDays) {
        this.jobTimeNeededDays = jobTimeNeededDays;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobDto)) return false;
        JobDto jobDto = (JobDto) o;
        return Objects.equals(jobId, jobDto.jobId) &&
               Objects.equals(dueDate, jobDto.dueDate) &&
               Objects.equals(startDate, jobDto.startDate) &&
               Objects.equals(endDate, jobDto.endDate) &&
               Objects.equals(materialNeeded, jobDto.materialNeeded) &&
               Objects.equals(materialAmount, jobDto.materialAmount) &&
               Objects.equals(jobTimeNeededDays, jobDto.jobTimeNeededDays) &&
               Objects.equals(priority, jobDto.priority)&&
                Objects.equals(title, jobDto.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, dueDate, startDate, endDate, materialNeeded,
                          materialAmount,jobTimeNeededDays, priority, title);
    }

    @Override
    public String toString() {
        return "JobDto{" +
               "jobId=" + jobId +
               ", dueDate=" + dueDate +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", materialNeeded='" + materialNeeded + '\'' +
               ", materialAmount=" + materialAmount +
               ", jobTimeNeededDays=" + jobTimeNeededDays +
               ", priority=" + priority +
                ", title='" + title + '\'' +
               '}';
    }
}
