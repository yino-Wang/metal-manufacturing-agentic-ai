package com.example.domain.model.entities;

import jakarta.persistence.*;

/**
 * MachineJobMapping
 * Maps machines to job roles for workforce planning
 */
@Entity
@Table(name = "machine_job_mapping")
public class MachineJobMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "machine_id", nullable = false)
    private String machineId;

    @Column(name = "production_line")
    private String productionLine;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "required_skill_level")
    private String requiredSkillLevel;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public MachineJobMapping() {}

    public MachineJobMapping(String machineId, String productionLine, Long jobId, String requiredSkillLevel) {
        this.machineId = machineId;
        this.productionLine = productionLine;
        this.jobId = jobId;
        this.requiredSkillLevel = requiredSkillLevel;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(String productionLine) {
        this.productionLine = productionLine;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public void setRequiredSkillLevel(String requiredSkillLevel) {
        this.requiredSkillLevel = requiredSkillLevel;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
