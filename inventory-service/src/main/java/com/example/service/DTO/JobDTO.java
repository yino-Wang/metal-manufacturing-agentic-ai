package com.example.dto;

/**
 * DTO sent from Business Service → Inventory Service.
 * Contains only the job ID, material ID, and amount required.
 */
public class JobDTO {

    private String jobId;          // Identifier of the business job
    private int materialId;        // Which material this job uses
    private int quantityRequired;  // Amount of material to consume

    public JobDTO() {}

    public JobDTO(String jobId, int materialId, int quantityRequired) {
        this.jobId = jobId;
        this.materialId = materialId;
        this.quantityRequired = quantityRequired;
    }

    // Getters and setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public int getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(int quantityRequired) { this.quantityRequired = quantityRequired; }

    @Override
    public String toString() {
        return "JobDTO{" +
                "jobId='" + jobId + '\'' +
                ", materialId=" + materialId +
                ", quantityRequired=" + quantityRequired +
                '}';
    }
}
