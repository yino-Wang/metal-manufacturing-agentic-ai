package com.example.infrastructure.repository;

import com.example.model.individualScheduleJobId;
import com.example.model.individualSchedule_job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface individualSchedule_jobRepository extends JpaRepository<individualSchedule_job, individualScheduleJobId> {
}
