package com.example.infrastructure.repository;

import com.example.domain.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Material, Integer> {
    Optional<Material> findByName(String name);
}
