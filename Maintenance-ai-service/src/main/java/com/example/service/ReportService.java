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
    public ReportDto getReport(String reportId) {
        Report report = this.reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        return mapReportToDto(report);
    }

    @Transactional
    public void addReport(ReportDto reportDto) {
        Report report = new Report();
        report = mapDtoToReport(reportDto);
        Report savedReport = reportRepository.save(report);
    }

    @Transactional
    public void removeReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        reportRepository.delete(report);
    }

    @Transactional
    public List<MachineDto> findAllMachines() {
        return this.machineRepository.findAll().stream()
                .map(this::mapMachineToDto)
                .toList();
    }


    @Transactional
    public MachineDto getMachine(String machineId) {
        Machine machine = this.machineRepository.findById(machineId)
                .orElseThrow(() -> new MachineNotFoundException(machineId));
        return mapMachineToDto(machine);
    }

    @Transactional
    public void addMachine(MachineDto machineDto) {
        Machine machine = new Machine();
        machine = mapDtoToMachine(machineDto);
        Machine savedMachine = machineRepository.save(machine);
    }

    @Transactional
    public void removeMachine(String machineId) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new MachineNotFoundException(machineId));
        reportRepository.deleteAll(machine.getMaintenanceReports());
        machineRepository.delete(machine);
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
        return new ReportDto(report.getReportId(), report.getReportDate(), report.getMachine().getMachineId(), report.getIssue(), report.getSolution());
    }

    @Transactional
    private Report mapDtoToReport(ReportDto dto) {
        Report report = new Report();
        report.setReportId(dto.reportId());
        report.setReportDate(dto.reportDate());
        Machine machine = machineRepository.findById(dto.machineId())
                .orElseThrow(() -> new MachineNotFoundException(dto.machineId()));
        report.setMachine(machine);
        report.setIssue(dto.issue());
        report.setSolution(dto.solution());
        return report;
    }

    @Transactional
    private Machine mapDtoToMachine(MachineDto dto) {
        Machine machine = new Machine();
        machine.setMachineId(dto.machineId());
        List<Report> reports = dto.reports().stream()
                .map(this::mapDtoToReport)
                .toList();
        machine.setMaintenanceReports(reports);
        return machine;
    }

}


