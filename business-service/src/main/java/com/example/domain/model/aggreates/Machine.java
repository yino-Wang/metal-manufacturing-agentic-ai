package com.example.domain.model.aggreates;

import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.domain.model.entities.Employee;
import com.example.domain.model.valueobjects.*;
import com.example.interfaces.rest.*;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.example.interfaces.rest.JobAddedToMachineEventData;
import com.example.interfaces.rest.JobAddedToMachineEvent;
import com.example.interfaces.rest.MachineScheduledEventData;
import com.example.interfaces.rest.MachineScheduledEvent;

@Entity
@NamedQueries({
        @NamedQuery(name = "Machine.findAll",
                query = "Select m from Machine m"),
        @NamedQuery(name = "Machine.findBySchedulingId",
                query = "Select m from Machine m where m.schedulingId = ?1"),
        @NamedQuery(name = "Machine.findAllSchedulingId",
                query = "Select m.schedulingId from Machine m"),
        @NamedQuery(name = "Machine.findByMachineName",
                query = "Select m from Machine m where m.machineName = ?1")})
public class Machine extends AbstractAggregateRoot<Machine> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private MachineName machineName; // Name of the Machine
    @Embedded
    private SchedulingId schedulingId; // Aggregate Identifier
    @Embedded
    private Employee employee; //Employee assigned to the Machine
    @Embedded
    private JobList jobList; //List of Jobs assigned to the Machine
    @Embedded
    private Schedule schedule; //Schedule of the Machine
    @Embedded
    private CurrentJob currentJob; //Current Job being handled by the Machine

    /**
     * Default Constructor
     */
    public Machine() { }

    /**
     * Constructor Command Handler for a new Machine scheduling. Sets the state of the Aggregate
     * and registers the Machine Scheduled Event
     *
     */
    public Machine(ScheduleMachineCommand scheduleMachineCommand) {
        this.schedulingId = new SchedulingId(scheduleMachineCommand.getSchedulingId());
        this.machineName = new MachineName(scheduleMachineCommand.getMachineName());
        this.employee = new Employee(scheduleMachineCommand.getEmployeeName());
        this.jobList = JobList.EMPTY_LIST; //Empty Job List since the Machine has no jobs assigned yet
        this.schedule = new Schedule(); //Empty Schedule since the Machine has no jobs scheduled yet

        // Registering the Machine Scheduled Event
        addDomainEvent(new
                MachineScheduledEvent(
                new MachineScheduledEventData(machineName.getMachineName(),
                        scheduleMachineCommand.getEmployeeName())
        ));
    }

    public SchedulingId getSchedulingId() {
        return schedulingId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public JobList getJobList() {
        return jobList;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Command Handler for the Route Cargo Command. Sets the state of the Aggregate and registers the
     * Cargo routed event
     * @param job
     */

    public void addJob(Job job) {
        this.jobList.getJobs().add(job);
        // Register a domain event representing that a job was added to the machine
        addDomainEvent(new
                JobAddedToMachineEvent(
                new JobAddedToMachineEventData(
                        this.schedulingId.getSchedulingId(),
                        job.getJobNumber(),
                        this.machineName.getMachineName(),
                        job.getSubmitDate(),
                        job.getMaterialNeeded(),
                        job.getMaterialAmount())));
    }

//    /**
//     *
//     * @param lastJobHandledEvent
//     */
//    public void deriveCurrentJobProgress(LastJobHandledEvent lastJobHandledEvent) {
//        this.currentJob = CurrentJob.derivedFrom(getJobList(),
//                lastJobHandledEvent);
//    }


    /**
     * Method to register the event
     * @param event
     */
    public void addDomainEvent(Object event){
        registerEvent(event);
    }

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", machineName=" + machineName +
                ", schedulingId=" + schedulingId +
                ", employee=" + employee +
                ", jobList=" + jobList +
                ", schedule=" + schedule +
                ", currentJob=" + currentJob +
                '}';
    }
}
