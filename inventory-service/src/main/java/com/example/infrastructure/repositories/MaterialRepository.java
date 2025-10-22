package com.example.infrastructure.repositories;

import com.example.domain.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    Material findByMaterialName(String materialName);
}
