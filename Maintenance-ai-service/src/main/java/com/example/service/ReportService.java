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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public void addReport(ReportDto reportDto) {
        Report report = new Report();
        report = mapDtoToReport(reportDto);
        Report savedReport = reportRepository.save(report);
    }

    @Transactional
    public void addMachine(MachineDto machineDto) {
        Machine machine = new Machine();
        machine = mapDtoToMachine(machineDto);
        Machine savedMachine = machineRepository.save(machine);
    }

    public void patchReport(String reportId, Map<String, Object> updates) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        // You can't patch reportId
        if (updates.containsKey("reportDate")) {
            report.setReportDate(LocalDate.parse((String) updates.get("reportDate")));
        }
        if (updates.containsKey("machineId")) {
            String machineId = (String) updates.get("machineId");
            Machine machine = machineRepository.findById(machineId)
                    .orElseThrow(() -> new MachineNotFoundException(machineId));
            report.setMachine(machine);
        }
        if (updates.containsKey("issue")) {
            report.setIssue((String) updates.get("issue"));
        }
        if (updates.containsKey("solution")) {
            report.setSolution((String) updates.get("solution"));
        }

        reportRepository.save(report);
    }

    public void patchMachine(String machineId, Map<String, Object> updates) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new MachineNotFoundException(machineId));

        // You can't patch machineId
        if (updates.containsKey("reports")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> reportUpdates = (List<Map<String, Object>>) updates.get("reports");
            List<Report> reports = reportUpdates.stream()
                    .map(reportMap -> {
                        String reportId = (String) reportMap.get("reportId");
                        Report report = reportRepository.findById(reportId)
                                .orElseThrow(() -> new ReportNotFoundException(reportId));
                        // Optionally patch report fields here if needed
                        return report;
                    })
                    .toList();
            machine.setMaintenanceReports(reports);
        }

        machineRepository.save(machine);
    }

    @Transactional
    public void removeReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        reportRepository.delete(report);
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


