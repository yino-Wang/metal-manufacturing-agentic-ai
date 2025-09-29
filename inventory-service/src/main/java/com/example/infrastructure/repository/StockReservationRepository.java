package com.example.infrastructure.repository;

import com.example.domain.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    // Custom query methods can be defined here
}
