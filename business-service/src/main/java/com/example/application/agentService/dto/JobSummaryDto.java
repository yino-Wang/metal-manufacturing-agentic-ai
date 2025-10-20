package com.example.application.agentService.dto;

import java.time.LocalDate;

public record JobSummaryDto(
        LocalDate dueDate,
        Integer jobNumber,
        Integer jobTimeNeededDays,
        Integer priority
) {}
