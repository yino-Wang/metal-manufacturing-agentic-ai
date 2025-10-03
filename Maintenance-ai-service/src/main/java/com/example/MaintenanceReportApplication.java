package com.example;

import com.example.infrastructure.repository.ReportRepository;
import com.example.infrastructure.repository.MachineRepository;
import com.example.model.Report;
import com.example.model.Machine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootApplication
public class MaintenanceReportApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MaintenanceReportApplication.class, args);
    }

}
