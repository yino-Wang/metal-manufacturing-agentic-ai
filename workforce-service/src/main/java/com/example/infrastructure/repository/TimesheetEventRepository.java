package com.example.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.domain.event.TimesheetEvent;

public interface TimesheetEventRepository extends JpaRepository<TimesheetEvent, Long> {
}
