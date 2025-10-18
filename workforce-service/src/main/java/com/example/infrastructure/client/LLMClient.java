package com.example.infrastructure.client;

import com.example.domain.model.entities.AgentInput;
import com.example.domain.model.entities.ShiftPlan;

import java.util.List;

public interface LLMClient {
    List<ShiftPlan> generateShiftPlan(AgentInput input);
}
