package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response model for machine schedule API
 */
public class MachineScheduleResponse {

    @JsonProperty("machineId")
    private MachineIdDto machineId;

    @JsonProperty("jobList")
    private JobListDto jobList;

    @JsonProperty("schedule")
    private ScheduleDto schedule;

    // Getters and setters
    public MachineIdDto getMachineId() {
        return machineId;
    }

    public void setMachineId(MachineIdDto machineId) {
        this.machineId = machineId;
    }

    public JobListDto getJobList() {
        return jobList;
    }

    public void setJobList(JobListDto jobList) {
        this.jobList = jobList;
    }

    public ScheduleDto getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDto schedule) {
        this.schedule = schedule;
    }

    public static class MachineIdDto {
        @JsonProperty("machineId")
        private String machineId;

        public String getMachineId() {
            return machineId;
        }

        public void setMachineId(String machineId) {
            this.machineId = machineId;
        }
    }

    public static class JobListDto {
        @JsonProperty("jobs")
        private List<JobDetailDto> jobs;

        public List<JobDetailDto> getJobs() {
            return jobs;
        }

        public void setJobs(List<JobDetailDto> jobs) {
            this.jobs = jobs;
        }
    }

    public static class ScheduleDto {
        @JsonProperty("jobs")
        private List<JobDetailDto> jobs;

        public List<JobDetailDto> getJobs() {
            return jobs;
        }

        public void setJobs(List<JobDetailDto> jobs) {
            this.jobs = jobs;
        }
    }

    public static class JobDetailDto {
        @JsonProperty("dueDate")
        private String dueDate;

        @JsonProperty("startDate")
        private String startDate;

        @JsonProperty("jobNumber")
        private Long jobNumber;

        @JsonProperty("endDate")
        private String endDate;

        @JsonProperty("materialNeeded")
        private String materialNeeded;

        @JsonProperty("materialAmount")
        private Integer materialAmount;

        @JsonProperty("jobTimeNeededDays")
        private Integer jobTimeNeededDays;

        @JsonProperty("priority")
        private Integer priority;

        @JsonProperty("customerName")
        private String customerName;

        // Getters and setters
        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public Long getJobNumber() {
            return jobNumber;
        }

        public void setJobNumber(Long jobNumber) {
            this.jobNumber = jobNumber;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
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

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }
}
