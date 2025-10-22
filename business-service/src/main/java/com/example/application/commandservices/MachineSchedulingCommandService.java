package com.example.application.commandservices;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.domain.model.commands.AddJobToMachineCommand;
import com.example.domain.model.entities.Job;
import com.example.infrastructure.repositories.MachineRepository;
import org.springframework.stereotype.Service;

@Service
public class MachineSchedulingCommandService {

    /**
     * Application service class for the machine scheduling commands
     */

    private final MachineRepository machineRepository;

    public MachineSchedulingCommandService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    /**
     * Service Command method to create a new machine
     *
     * @return machineId of the machine
     */

    public MachineId scheduleMachine(ScheduleMachineCommand scheduleMachineCommand) {

        Machine machine = new Machine(scheduleMachineCommand);
        machineRepository.save(machine);
        return new MachineId(scheduleMachineCommand.getMachineName());
    }

    /**
     * Service Command method to assign a job to a machine
     * @param addJobToMachineCommand
     */

    public Job addJobToMachine(AddJobToMachineCommand addJobToMachineCommand) {
        Machine machine = machineRepository.findByMachineId(
                new MachineId(addJobToMachineCommand.getMachineId()));

        machine.addJob(new
                Job(addJobToMachineCommand.getJobNumber(), addJobToMachineCommand.getJobTimeNeededDays(),
                addJobToMachineCommand.getPriority(),
                addJobToMachineCommand.getDueDate(), addJobToMachineCommand.getMaterialNeeded(),
                addJobToMachineCommand.getMaterialAmount(), addJobToMachineCommand.getCustomerName()));
        machineRepository.save(machine);
        return new Job(addJobToMachineCommand.getJobNumber(), addJobToMachineCommand.getJobTimeNeededDays(),
                addJobToMachineCommand.getPriority(),
                addJobToMachineCommand.getDueDate(), addJobToMachineCommand.getMaterialNeeded(),
                addJobToMachineCommand.getMaterialAmount(), addJobToMachineCommand.getCustomerName());
    }
}
