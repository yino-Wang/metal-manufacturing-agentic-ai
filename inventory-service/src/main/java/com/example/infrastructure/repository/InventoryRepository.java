package com.example.infrastructure.repository;

import com.example.domain.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository to manage Material persistence in H2.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Material, Integer> { }
