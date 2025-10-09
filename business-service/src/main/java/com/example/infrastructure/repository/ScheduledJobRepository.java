package com.example.infrastructure.repository;

import com.example.domain.model.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledJobRepository  extends JpaRepository<ScheduledJob, Long> {
    ScheduledJob findByJobId(Long jobId);

    List<Long> findAllJobIds();

    List<ScheduledJob> findAll();
}
