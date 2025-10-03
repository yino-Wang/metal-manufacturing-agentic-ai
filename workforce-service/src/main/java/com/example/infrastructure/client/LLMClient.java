package com.example.infrastructure.client;

import com.example.domain.model.AgentInput;
import com.example.domain.model.ShiftSchedule;

import java.util.List;

public interface LLMClient {
    List<ShiftSchedule> generateShiftPlan(AgentInput input);
}
