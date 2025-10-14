package com.example.infrastructure.repository;

import com.example.domain.model.aggregates.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Find job by exact title match
     */
    Optional<Job> findByTitle(String title);

    /**
     * Find jobs by title containing keyword (case insensitive)
     */
    @Query("SELECT j FROM Job j WHERE UPPER(j.title) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    Optional<Job> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * Find all jobs by title containing keyword
     */
    @Query("SELECT j FROM Job j WHERE UPPER(j.title) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<Job> findAllByTitleContaining(@Param("keyword") String keyword);

    /**
     * Check if job exists by title
     */
    boolean existsByTitle(String title);
}
