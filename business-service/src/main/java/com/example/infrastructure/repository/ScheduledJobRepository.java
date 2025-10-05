package com.example.infrastructure.repository;

import com.example.model.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledJobRepository  extends JpaRepository<ScheduledJob, Long> {
}
