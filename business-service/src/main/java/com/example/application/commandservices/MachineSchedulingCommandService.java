package com.example.application.commandservices;

import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.SchedulingId;
import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.domain.model.commands.AddJobToMachineScheduleCommand;
import com.example.infrastructure.repositories.MachineRepository;

import java.util.UUID;

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

    public SchedulingId scheduleMachine(ScheduleMachineCommand scheduleMachineCommand) {

        String random = UUID.randomUUID().toString().toUpperCase();
        String scheduleIdStr = random.substring(0, random.indexOf("-"));
        System.out.println("Random is :" + scheduleIdStr);
        scheduleMachineCommand.setSchedulingId(scheduleIdStr);
        Machine machine = new Machine(scheduleMachineCommand);
        machineRepository.save(machine);
        return new SchedulingId(scheduleIdStr);
    }

    /**
     * Service Command method to assign a job to a machine
     * @param addJobToMachineScheduleCommand
     */

    public void assignJobToMachine(AddJobToMachineScheduleCommand addJobToMachineScheduleCommand) {
        System.out.println("Update Machine Schedule Command" + addJobToMachineScheduleCommand.getSchedulingId());
        Machine machine = machineRepository.findBySchedulingId(
                new SchedulingId(addJobToMachineScheduleCommand.getSchedulingId()));
        machine.assignJob(addJobToMachineScheduleCommand.getJobId(), addJobToMachineScheduleCommand.getJobDescription());
        machineRepository.save(machine);
    }
}
