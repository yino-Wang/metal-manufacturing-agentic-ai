package com.example.infrastructure.repository;

import com.example.domain.model.entities.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeId(Long employeeId);
    List<Payslip> findByEmployeeIdAndStartDate(Long employeeId, Date startDate);
}
