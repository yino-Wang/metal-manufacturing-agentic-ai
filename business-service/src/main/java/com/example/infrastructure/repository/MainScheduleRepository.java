package com.example.infrastructure.repository;

import com.example.model.MainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainScheduleRepository  extends JpaRepository<MainSchedule, Long> {
}
