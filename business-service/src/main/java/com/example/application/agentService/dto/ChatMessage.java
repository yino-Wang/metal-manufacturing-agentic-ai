package com.example.application.agentService.dto;

public record ChatMessage(State state,
                          String messageToCustomer,
                          String scheduleMachineName) {}
