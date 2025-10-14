package com.example.presentation.controller;


import com.example.application.agentService.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }

    @GetMapping("/chat-generateSchedule")
    public String getSchedule(@RequestParam String sessionId, @RequestParam String userMessage) {
        return this.scheduleService.generateSchedule(sessionId, userMessage);
    }
}