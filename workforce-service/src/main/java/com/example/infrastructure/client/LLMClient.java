package com.example.infrastructure.client;

import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftSchedule;

import java.util.List;

public interface LLMClient {
    List<ShiftSchedule> generateShiftPlan(AgentInput input);
}
