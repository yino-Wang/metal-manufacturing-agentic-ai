package com.example.presentation.controller;

import com.example.service.dto.MachineDto;
import com.example.service.dto.ReportDto;
import com.example.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin/reports/{reportId}")
    public ReportDto getReportById(@PathVariable String reportId) {
        return reportService.getReport(reportId);
    }

    @GetMapping("/admin/reports")
    public List<ReportDto> getAllReports() {
        return reportService.findAllReports();
    }

    @GetMapping("/admin/machines/{machineId}")
    public MachineDto getMachineById(@PathVariable String machineId) {
        return reportService.getMachine(machineId);
    }

    @GetMapping("/admin/machines")
    public List<MachineDto> getAllMachines() {
        return reportService.findAllMachines();
    }

}
