package com.example.presentation.controller;

import com.example.service.dto.MachineDto;
import com.example.service.dto.ReportDto;
import com.example.service.ReportService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/admin/reports")
    public void addReport(@RequestBody ReportDto reportDto) {
        reportService.addReport(reportDto);
    }

    @DeleteMapping("/admin/reports/{reportId}")
    public void removeReport(@PathVariable String reportId) {
        reportService.removeReport(reportId);
    }

    @GetMapping("/admin/machines/{machineId}")
    public MachineDto getMachineById(@PathVariable String machineId) {
        return reportService.getMachine(machineId);
    }

    @GetMapping("/admin/machines")
    public List<MachineDto> getAllMachines() {
        return reportService.findAllMachines();
    }

    @PostMapping("/admin/machines")
    public void addMachine(@RequestBody MachineDto machineDto) {
        reportService.addMachine(machineDto);
    }

    @DeleteMapping("/admin/machines/{machineId}")
    public void removeMachine(@PathVariable String machineId) {
        reportService.removeMachine(machineId);
    }

}
