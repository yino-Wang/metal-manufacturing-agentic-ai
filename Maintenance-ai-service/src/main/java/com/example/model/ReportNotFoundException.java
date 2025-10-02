package com.example.model;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String reportId) {
        super("Report " + reportId + " not found");
    }
}
