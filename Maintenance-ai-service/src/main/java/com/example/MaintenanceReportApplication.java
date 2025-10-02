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

    @Bean
    public CommandLineRunner loadDatabase(ReportRepository reportRepository, MachineRepository machineRepository) {
        return args -> {
            Machine machineA = new Machine("Machine A", new ArrayList<>());
            Machine machineB = new Machine("Machine B", new ArrayList<>());

            machineRepository.save(machineA);
            machineRepository.save(machineB);

            reportRepository.save(new Report("A1", LocalDate.now().minusDays(10), machineA));
            reportRepository.save(new Report("A2", LocalDate.now().minusDays(5), machineA));
            reportRepository.save(new Report("B1", LocalDate.now(), machineB));
        };
    }

}
