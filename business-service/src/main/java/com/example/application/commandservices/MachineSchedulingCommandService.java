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
     * Service Command method to schedule a new machine
     *
     * @return scheudlingId of the machine
     */

    public MachineId scheduleMachine(ScheduleMachineCommand scheduleMachineCommand) {

        //String random = UUID.randomUUID().toString().toUpperCase();
        //String scheduleIdStr = random.substring(0, random.indexOf("-"));
        //System.out.println("Random is :" + scheduleIdStr);
        //scheduleMachineCommand.setSchedulingId(scheduleIdStr);
        Machine machine = new Machine(scheduleMachineCommand);
        machineRepository.save(machine);
        return new MachineId(scheduleMachineCommand.getMachineName());
    }

    /**
     * Service Command method to assign a job to a machine
     * @param addJobToMachineCommand
     */

    public Job addJobToMachine(AddJobToMachineCommand addJobToMachineCommand) {
        //System.out.println("Adding job: " + addJobToMachineCommand);
        //System.out.println("****Adding Job to Machine ****" + addJobToMachineCommand.getJobNumber());
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
