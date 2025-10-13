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

    @GetMapping("/admin/reports/{reportId}")
    public ReportDto getReportById(@PathVariable String reportId) {
        return reportService.getReport(reportId);
    }

    @GetMapping("/admin/machines/{machineId}")
    public MachineDto getMachineById(@PathVariable String machineId) {
        return reportService.getMachine(machineId);
    }

    @GetMapping("/admin/reports")
    public List<ReportDto> getAllReports() {
        return reportService.findAllReports();
    }

    @GetMapping("/admin/machines")
    public List<MachineDto> getAllMachines() {
        return reportService.findAllMachines();
    }

    @PostMapping("/admin/reports")
    public void addReport(@RequestBody ReportDto reportDto) {
        reportService.addReport(reportDto);
    }

    @PostMapping("/admin/machines")
    public void addMachine(@RequestBody MachineDto machineDto) {
        reportService.addMachine(machineDto);
    }

    @PatchMapping("/admin/reports/{reportId}")
    public void patchReport(@PathVariable String reportId, @RequestBody Map<String, Object> updates) {
        reportService.patchReport(reportId, updates);
    }

    @PatchMapping("/admin/machines/{machineId}")
    public void patchMachine(@PathVariable String machineId, @RequestBody Map<String, Object> updates) {
        reportService.patchMachine(machineId, updates);
    }

    @DeleteMapping("/admin/reports/{reportId}")
    public void removeReport(@PathVariable String reportId) {
        reportService.removeReport(reportId);
    }

    @DeleteMapping("/admin/machines/{machineId}")
    public void removeMachine(@PathVariable String machineId) {
        reportService.removeMachine(machineId);
    }

}
