package com.example.presentation.controller;


import com.example.application.agentService.ScheduleService;
import com.example.application.queryservices.MachineSchedulingQueryService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Job;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }

    @GetMapping("/chat-generateSchedule/{machineId}")
    public Schedule generateSchedule(@RequestParam String machineId) {
        return this.scheduleService.generateSchedule(machineId);
    }
}