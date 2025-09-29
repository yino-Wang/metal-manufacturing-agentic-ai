package com.example.service.DTO;

/**
 * DTO for reserved stock request
 */
public class ReservedStockRequestDTO {
    private Long materialId;
    private int quantity;
    private Long jobId; // ID of the job for which stock is reserved
    private String location;

    public ReservedStockRequestDTO() {}

    public ReservedStockRequestDTO(Long materialId, int quantity, Long jobId, String location) {
        this.materialId = materialId;
        this.quantity = quantity;
        this.jobId = jobId;
        this.location = location;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
