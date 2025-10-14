package com.example.service.dto;

import java.time.LocalDate;

public record ReportDto(String reportId, LocalDate reportDate, String machineId, String issue, String solution) {
}
