package com.example.infrastructure.repository;

import com.example.domain.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material,Long> {
}
