package com.example.application.agentService.dto;

import java.time.LocalDate;
import java.util.List;

public record JobListDto(List<JobSummaryDto> jobs, LocalDate currentDate) {}
