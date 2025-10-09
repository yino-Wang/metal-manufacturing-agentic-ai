package com.example.infrastructure.repository;

import com.example.domain.model.MainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainScheduleRepository  extends JpaRepository<MainSchedule, Long> {
    MainSchedule findTopByOrderByIdDesc();
}
