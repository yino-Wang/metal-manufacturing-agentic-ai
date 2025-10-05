package com.example.infrastructure.repository;

import com.example.model.IndividualSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualScheduleRepository extends JpaRepository<IndividualSchedule, Long> {
}
