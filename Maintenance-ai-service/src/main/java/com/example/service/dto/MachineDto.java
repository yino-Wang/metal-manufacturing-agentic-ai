package com.example.service.dto;

import java.util.List;

public record MachineDto(String machineId, List<ReportDto> reports) {
}
