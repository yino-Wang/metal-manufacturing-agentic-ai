package com.example.application.agentService.dto;

import com.example.domain.model.valueobjects.Schedule;

public record ChatMessage(String messageToCustomer,
                          Schedule schedule) {}
