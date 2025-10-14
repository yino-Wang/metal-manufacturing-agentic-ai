package com.example.infrastructure.repository;

import com.example.domain.model.entities.MachineJobMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MachineJobMappingRepository extends JpaRepository<MachineJobMapping, Long> {

    /**
     * Find mapping by machine ID and production line
     */
    @Query("SELECT m FROM MachineJobMapping m WHERE m.machineId = :machineId AND m.productionLine = :productionLine AND m.isActive = true")
    Optional<MachineJobMapping> findByMachineIdAndProductionLine(@Param("machineId") String machineId, @Param("productionLine") String productionLine);

    /**
     * Find mapping by machine ID only
     */
    Optional<MachineJobMapping> findByMachineIdAndIsActive(String machineId, Boolean isActive);

    /**
     * Find all mappings for a production line
     */
    List<MachineJobMapping> findByProductionLineAndIsActive(String productionLine, Boolean isActive);

    /**
     * Check if mapping exists for machine
     */
    boolean existsByMachineIdAndIsActive(String machineId, Boolean isActive);
}
