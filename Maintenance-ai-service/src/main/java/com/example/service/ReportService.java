package com.example.service;

import com.example.infrastructure.repository.ReportRepository;
import com.example.model.Report;
import com.example.model.Machine;
import com.example.service.dto.ReportDto;
import com.example.service.dto.MachineDto;
import com.example.model.ReportNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public String recommend(String sessionId, String report) {
        return "Report received: " + report;
    }

    @Transactional
    public ReportDto getReport(String reportId) {
        Report report = this.reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        return mapReportToDto(report);
    }

    @Transactional
    private MachineDto mapMachineToDto(Machine machine) {
        List<ReportDto> reportDtos = machine.getMaintenanceReports().stream()
                .map(this::mapReportToDto)
                .toList();
        return new MachineDto(machine.getMachineId(), reportDtos);
    }

    @Transactional
    private ReportDto mapReportToDto(Report report) {
        return new ReportDto(report.getReportId(), report.getReportDate(), report.getMachine().getMachineId());
    }

}


