package com.example.service;

import com.example.infrastructure.repository.ReportRepository;
import com.example.infrastructure.repository.MachineRepository;
import com.example.model.Report;
import com.example.model.Machine;
import com.example.service.dto.ReportDto;
import com.example.service.dto.MachineDto;
import com.example.model.ReportNotFoundException;
import com.example.model.MachineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final MachineRepository machineRepository;

    public ReportService(ReportRepository reportRepository, MachineRepository machineRepository) {
        this.reportRepository = reportRepository;
        this.machineRepository = machineRepository;
    }

    public String recommend(String sessionId, String report) {
        return "Report received: " + report;
    }

    public List<ReportDto> findAllReports() {
        return this.reportRepository.findAll().stream()
                .map(this::mapReportToDto)
                .toList();
    }

    @Transactional
    public List<MachineDto> findAllMachines() {
        return this.machineRepository.findAll().stream()
                .map(this::mapMachineToDto)
                .toList();
    }

    @Transactional
    public ReportDto getReport(String reportId) {
        Report report = this.reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        return mapReportToDto(report);
    }

    @Transactional
    public MachineDto getMachine(String machineId) {
        Machine machine = this.machineRepository.findById(machineId)
                .orElseThrow(() -> new MachineNotFoundException(machineId));
        return mapMachineToDto(machine);
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


