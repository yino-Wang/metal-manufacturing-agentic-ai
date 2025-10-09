package com.example.application.queryservices;

import com.example.domain.model.*;
import com.example.infrastructure.repository.*;
import com.example.domain.model.valueObjects.Machine;

import java.util.List;

public class SchedulesQueryService {

    //inject dependencies
    private MachineScheduleRepository machineScheduleRepository;
    private IndividualScheduleRepository individualScheduleRepository;
    private EmployeeRepository employeeRepository;
    private MainScheduleRepository mainScheduleRepository;
    private ScheduledJobRepository scheduledJobRepository;
    private ScheduledProductionStepRepository scheduledProductionStepRepository;

    public SchedulesQueryService(MachineScheduleRepository machineScheduleRepository,
                                 IndividualScheduleRepository individualScheduleRepository,
                                 EmployeeRepository employeeRepository,
                                 MainScheduleRepository mainScheduleRepository,
                                 ScheduledJobRepository scheduledJobRepository,
                                 ScheduledProductionStepRepository scheduledProductionStepRepository) {
        this.machineScheduleRepository = machineScheduleRepository;
        this.individualScheduleRepository = individualScheduleRepository;
        this.employeeRepository = employeeRepository;
        this.mainScheduleRepository = mainScheduleRepository;
        this.scheduledJobRepository = scheduledJobRepository;
        this.scheduledProductionStepRepository = scheduledProductionStepRepository;
    }

    //individualScheduleRepository methods
    public IndividualSchedule findIndividualSchedule(Employee employee) { return individualScheduleRepository.findByEmployee(employee); }
    public List<Employee> findAllEmployees() { return individualScheduleRepository.findAllEmployees(); }
    public List<IndividualSchedule> findAllIndividualSchedules() { return individualScheduleRepository.findAll(); }

    //machineSchedule methods
    public MachineSchedule findMachineSchedule(Machine machine) { return machineScheduleRepository.findByMachine(machine); }
    public List<Machine> findAllMachines() { return machineScheduleRepository.findAllMachines(); }
    public List<MachineSchedule> findAllMachineSchedules() { return machineScheduleRepository.findAll(); }

    //MainScheduleRepository methods
    public MainSchedule find() { return mainScheduleRepository.findTopByOrderByIdDesc(); }

    //scheduledJobRepository methods
    public ScheduledJob findScheduledJob(Long jobId) { return scheduledJobRepository.findByJobId(jobId); }
    public List<Long> findAllJobIds() { return scheduledJobRepository.findAllJobIds(); }
    public List<ScheduledJob> findAllScheduledJobs() { return scheduledJobRepository.findAll(); }

    //scheduledProductionStepRepository methods
    public ScheduledProductionStep findSte0(Long stepId) { return scheduledProductionStepRepository.findByStepId(stepId); }
    public List<Long> findAllStepIds() { return scheduledProductionStepRepository.findAllStepIds(); }
    public List<ScheduledProductionStep> findAll() { return scheduledProductionStepRepository.findAll(); }

}
