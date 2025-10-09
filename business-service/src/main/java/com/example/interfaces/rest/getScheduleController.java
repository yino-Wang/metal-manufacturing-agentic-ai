package com.example.interfaces.rest;

import com.example.application.queryservices.SchedulesQueryService;
import com.example.domain.model.Employee;
import com.example.domain.model.IndividualSchedule;
import com.example.domain.model.MachineSchedule;
import com.example.domain.model.MainSchedule;
import com.example.domain.model.valueObjects.Machine;
import com.example.infrastructure.repository.EmployeeRepository;
import com.example.infrastructure.repository.IndividualScheduleRepository;
import com.example.infrastructure.repository.MachineScheduleRepository;
import com.example.infrastructure.repository.MainScheduleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public class getScheduleController {
    private MainScheduleRepository mainScheduleRepository;
    private MachineScheduleRepository machineScheduleRepository;
    private IndividualScheduleRepository individualScheduleRepository;
    private EmployeeRepository employeeRepository;

    private SchedulesQueryService schedulesQueryService;

    //-----------main schedule -------------------------------------------//
    //get main schedule
    @GetMapping("/mainSchedule")
    @ResponseBody
    public MainSchedule getMainSchedule() {
        return mainScheduleRepository.findTopByOrderByIdDesc();
    }

    //-----------machine schedule -------------------------------------------//
    //get machine schedule by machine id
    @GetMapping("/machineSchedule/{machineId}")
    @ResponseBody
    public MachineSchedule getMachineScheduleByMachineId(@PathVariable String machineId) {
        return machineScheduleRepository.findByMachine(machineId);
    }

    //get all machine schedules
    @GetMapping("/machineSchedules")
    @ResponseBody
    public List<MachineSchedule> getAllMachineSchedules() {
        return machineScheduleRepository.findAll();
    }

    //get all machines
    @GetMapping("/machines")
    @ResponseBody
    public List<Machine> getAllMachines() {
        return machineScheduleRepository.findAllMachines();
    }

    //-----------individual schedule -------------------------------------------//
    //get individual schedule by employee id
    @GetMapping("/individualSchedule/{employeeId}")
    @ResponseBody
    public IndividualSchedule getIndividualScheduleByEmployeeId(@PathVariable Employee employeeId) {
        return individualScheduleRepository.findByEmployee(employeeId);
    }

    //get all individual schedules
    @GetMapping("/individualSchedules")
    @ResponseBody
    public List<IndividualSchedule> getAllIndividualSchedules() {
        return individualScheduleRepository.findAll();
    }

    //get all employees with schedules
    @GetMapping("/scheduledEmployees")
    @ResponseBody
    public List<Employee> getAllScheduledEmployees() {
        return individualScheduleRepository.findAllEmployees();
    }

    //-----------employees -------------------------------------------//
    //get all employees
    @GetMapping("/employees")
    @ResponseBody
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    //get employee by id
    @GetMapping("/employee/{id}")
    @ResponseBody
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id);
    }

}
