package com.example.infrastructure.repository;

import com.example.model.ScheduledProductionStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledProductionStepRepository  extends JpaRepository<ScheduledProductionStep, Long> {
}
