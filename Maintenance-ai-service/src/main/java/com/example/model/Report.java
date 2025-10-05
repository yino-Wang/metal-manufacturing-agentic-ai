package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Report {
    @Id
    private String reportId;
    @Column
    private LocalDate reportDate;
    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;
    @Column
    private String issue;
    @Column
    private String solution;

    public Report() {
    }

    public Report(String reportId, LocalDate reportDate, Machine machine, String issue, String solution) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        this.machine = machine;
        this.issue = issue;
        this.solution = solution;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public String getIssue() { return issue; }

    public void setIssue(String issue) { this.issue = issue; }

    public String getSolution() { return solution; }

    public void setSolution(String solution) { this.solution = solution; }

    @Override
    public String toString() {
        return "MaintenanceReport{" +
                "reportId='" + reportId + '\'' +
                ", reportDate=" + reportDate +
                ", machine=" + machine +
                '}';
    }
}
