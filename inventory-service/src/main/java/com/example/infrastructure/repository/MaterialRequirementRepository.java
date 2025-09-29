package com.example.infrastructure.repository;

import com.example.domain.model.MaterialRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRequirementRepository extends JpaRepository<MaterialRequirement, Long> {
}
