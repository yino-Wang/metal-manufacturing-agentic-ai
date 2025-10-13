package com.example.service.dto;

public record ChatMessage(State state,
                          String messageToUser,
                          String issueDescription) {
}
