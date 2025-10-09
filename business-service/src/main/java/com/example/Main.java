package com.example;

import com.example.domain.model.*;
import com.example.domain.model.valueObjects.Consumable;
import com.example.domain.model.valueObjects.Machine;
import com.example.domain.model.valueObjects.MachineStatus;
import com.example.domain.model.valueObjects.Status;
import com.example.infrastructure.repository.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;  //ADD THIS
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.Date;


@SpringBootApplication //ADD THIS
public class Main {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Main.class, args); //ADD THIS BUT CHANGE *MAIN* TO WHATEVER YOUR FILE IS CALLED
        System.out.printf("Hello and welcome!");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner loadDatabase(EmployeeRepository employeeRepository, IndividualScheduleRepository individualScheduleRepository,
                                          MachineScheduleRepository machineScheduleRepository, MainScheduleRepository mainScheduleRepository,
                                          ScheduledJobRepository scheduledJobRepository, ScheduledProductionStepRepository scheduledProductionStepRepository)
            throws Exception {
        return args -> {
            Employee emp1 = new Employee();
            emp1.setId(1L);
            emp1.setName("John Smith");
            employeeRepository.save(emp1);
            System.out.println(employeeRepository.findById(1L));

            Employee emp2 = new Employee();
            emp2.setId(2L);
            emp2.setName("Mary Jane");
            employeeRepository.save(emp2);
            System.out.println(employeeRepository.findById(2L));

            //preload machines
            Machine machine1 = new Machine();
            machine1.setMachineType("Type B");
            machine1.setName("Machine A");
            machine1.setRequiredWorkers("1");
            machine1.setMachineStatus(MachineStatus.STANDBY);

            Machine machine2 = new Machine();
            machine2.setMachineType("Type A");
            machine2.setName("Machine B");
            machine2.setRequiredWorkers("2");
            machine2.setMachineStatus(MachineStatus.BEING_FIXED);

            Machine machine3 = new Machine();
            machine3.setMachineType("Type B");
            machine3.setName("Machine C");
            machine3.setRequiredWorkers("1");
            machine3.setMachineStatus(MachineStatus.IN_USE);

            //preload machine schedules also saves machines
            MachineSchedule machineSchedule1 = new MachineSchedule();
            machineSchedule1.setMachineScheduleId(machine1.getName());
            machineSchedule1.setMachine(machine1);
            machineScheduleRepository.save(machineSchedule1);
            System.out.println(machineScheduleRepository.findByMachine("machine1"));

            MachineSchedule machineSchedule2 = new MachineSchedule();
            machineSchedule2.setMachineScheduleId(machine2.getName());
            machineSchedule2.setMachine(machine2);
            machineScheduleRepository.save(machineSchedule2);
            System.out.println(machineScheduleRepository.findByMachine("machine2"));

            MachineSchedule machineSchedule3 = new MachineSchedule();
            machineSchedule3.setMachineScheduleId(machine3.getName());
            machineSchedule3.setMachine(machine3);
            machineScheduleRepository.save(machineSchedule3);
            System.out.println(machineScheduleRepository.findByMachine("machine3"));

            //preload main schedule
            MainSchedule mainSchedule1 = new MainSchedule();
            mainSchedule1.setId(1L);

            //preload scheduled Jobs
            ScheduledJob job1 = new ScheduledJob();
            job1.setScheduledJobId(1L);
            job1.setMainSchedule(mainSchedule1);
            job1.setCustomer("Customer A");
            job1.setDueDate(new Date(2026, 6, 30));
            job1.setPriority(1);
            job1.setStatus(Status.NOT_STARTED);
            job1.setStartDate(new Date(2025, 3, 1));
            job1.setEndDate(new Date(2026, 6, 15));

            //preload consumables
            Consumable consumable1 = new Consumable();
            consumable1.setName("Screws");

            Consumable consumable2 = new Consumable();
            consumable2.setName("Nails");

            //preload scheduled production steps
            ScheduledProductionStep step1 = new ScheduledProductionStep();
            step1.setId(1L);
            step1.setStepName("Cutting");
            step1.setMachineHours(5);
            step1.setManHours(6);
            step1.addConsumable(consumable1);
            step1.addConsumable(consumable2);
            step1.setMachine(machine1);
            step1.setEmployee(emp2);
            step1.setReqMachineType("Type B");
            step1.setDependentOn();
            step1.setScheduledJob(job1);
            step1.setIndividualSchedule();
            step1.setMachineSchedule(machineSchedule3);
            step1.setStartTime(new Date(2025, 6, 30));
            step1.setEndTime(new Date(2025, 8, 20));
            step1.setStatus(Status.NOT_STARTED);
            step1.setPriority(1);
            step1.setQueueOrderNumber(step1.getMachineSchedule().getScheduledProductionSteps().indexOf(step1));





            job1.addScheduledProductionStep(step1);
            scheduledJobRepository.save(job1);
            System.out.println(scheduledJobRepository.findById(1L));




            mainSchedule1.addScheduledJob(job1);
            mainScheduleRepository.save(mainSchedule1);
            System.out.println(mainScheduleRepository.findById(1L).orElseThrow(() -> new Exception("Main Schedule not saved")));

        };
    }


}