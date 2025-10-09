package com.example.infrastructure.repository;

import com.example.model.ScheduledJob;
import com.example.model.ScheduledProductionStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledProductionStepRepository  extends JpaRepository<ScheduledProductionStep, Long> {
    ScheduledProductionStep findByStepId(String stepId);

    List<Long> findAllStepIds();

    List<ScheduledProductionStep> findAll();
}
