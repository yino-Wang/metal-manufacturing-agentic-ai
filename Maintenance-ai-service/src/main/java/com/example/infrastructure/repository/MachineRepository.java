package com.example.infrastructure.repository;

import com.example.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, String> {
}