package com.example.controller;

import com.example.service.dto.MachineDto;
import com.example.service.dto.ReportDto;
import com.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService){
        this.reportService = reportService;
    }

    @GetMapping("/maintenance/reports/{reportId}")
    public ReportDto getReportById(@PathVariable String reportId) {
        return reportService.getReport(reportId);
    }

    @GetMapping("/maintenance/machines/{machineId}")
    public MachineDto getMachineById(@PathVariable String machineId) {
        return reportService.getMachine(machineId);
    }

    @GetMapping("/maintenance/reports")
    public List<ReportDto> getAllReports() {
        return reportService.findAllReports();
    }

    @GetMapping("/maintenance/machines")
    public List<MachineDto> getAllMachines() {
        return reportService.findAllMachines();
    }

    @PostMapping("/maintenance/reports")
    public void addReport(@RequestBody ReportDto reportDto) {
        reportService.addReport(reportDto);
    }

    @PostMapping("/maintenance/machines")
    public void addMachine(@RequestBody MachineDto machineDto) {
        reportService.addMachine(machineDto);
    }

    @PatchMapping("/maintenance/reports/{reportId}")
    public void patchReport(@PathVariable String reportId, @RequestBody Map<String, Object> updates) {
        reportService.patchReport(reportId, updates);
    }

    @PatchMapping("/maintenance/machines/{machineId}")
    public void patchMachine(@PathVariable String machineId, @RequestBody Map<String, Object> updates) {
        reportService.patchMachine(machineId, updates);
    }

    @DeleteMapping("/maintenance/reports/{reportId}")
    public void removeReport(@PathVariable String reportId) {
        reportService.removeReport(reportId);
    }

    @DeleteMapping("/maintenance/machines/{machineId}")
    public void removeMachine(@PathVariable String machineId) {
        reportService.removeMachine(machineId);
    }

}
