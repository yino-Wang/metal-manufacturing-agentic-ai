package com.example.infrastructure.repository;

import com.example.domain.model.aggregates.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
