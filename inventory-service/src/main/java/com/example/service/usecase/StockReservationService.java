package com.example.service.usecase;


import com.example.domain.event.StockReserved;
import com.example.domain.event.StockUpdated;
import com.example.domain.exception.MaterialNotFoundException;
import com.example.domain.model.Material;
import com.example.domain.model.MaterialRequirement;
import com.example.domain.model.StockReservation;
import com.example.infrastructure.repository.MaterialRepository;
import com.example.infrastructure.repository.MaterialRequirementRepository;
import com.example.infrastructure.repository.StockReservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class StockReservationService {
    private final MaterialRepository materialRepository;
    private final StockReservationRepository stockReservationRepository;
    private final MaterialRequirementRepository materialRequirementRepository;
    private final RestTemplate restTemplate;
    private ApplicationEventPublisher eventPublisher;

    public StockReservationService(MaterialRepository materialRepository, StockReservationRepository stockReservationRepository, MaterialRequirementRepository materialRequirementRepository, RestTemplate restTemplate, ApplicationEventPublisher eventPublisher) {
        this.materialRepository = materialRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.materialRequirementRepository = materialRequirementRepository;
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void reserveStock(Long materialId, int reservedQuantity, Long reservedForJobId, String location, LocalDateTime reservationTime) {
        // Business logic to record timesheet
        // 1. Validate employee exists
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found"));
        if (material.getCurrentQuantity() < reservedQuantity) {
            // 2. Create and save stock reservation or material requirement
            MaterialRequirement materialRequirement = new MaterialRequirement();
            materialRequirement.setMaterialRequiredId(materialId);
            materialRequirement.setRequiredLocation(location);
            materialRequirement.setRequiredMaterial(material.getName());
            materialRequirement.setRequiredQuantity(reservedQuantity - material.getCurrentQuantity());
            materialRequirement.setForScheduledJobId(reservedForJobId);
            materialRequirementRepository.save(materialRequirement);

            throw new RuntimeException("Insufficient stock available");
            // save materialRequirement to DB
        } else {
            // Sufficient stock available, update material stock and create reservation
            material.setCurrentQuantity(material.getCurrentQuantity() - reservedQuantity);
            materialRepository.save(material);

            StockReservation stockReservation = new StockReservation();
            stockReservation.setMaterialId(materialId);
            stockReservation.setReservedQuantity(reservedQuantity);
            stockReservation.setReservedForJobId(reservedForJobId);
            stockReservation.setReservationTime(reservationTime);
            stockReservation.setLocation(location);
            stockReservationRepository.save(stockReservation);

        }
        // 3. Publish event
        StockReserved stockReservedEvent = new StockReserved(
                material.getId(),
                reservedQuantity,
                reservedForJobId,
                reservationTime,
                location,
                "STOCK_RESERVED"
        );
        eventPublisher.publishEvent(stockReservedEvent);

        StockUpdated stockUpdatedEvent = new StockUpdated(
                material.getId(),
                material.getName(),
                reservedQuantity,
                location,
                "RESERVED",
                "system", // assuming system user for this operation
                reservationTime,
                "STOCK_UPDATED"
        );
        eventPublisher.publishEvent(stockUpdatedEvent);
    }


}

