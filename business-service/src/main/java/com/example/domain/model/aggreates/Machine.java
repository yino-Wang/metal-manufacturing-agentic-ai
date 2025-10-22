package com.example.domain.model.aggreates;

import com.example.domain.model.commands.ScheduleMachineCommand;
//import com.example.domain.model.entities.Employee;
import com.example.domain.model.entities.Job;
import com.example.domain.model.valueobjects.*;
import com.example.events.JobAddedToMachineEvent;
import com.example.events.JobAddedToMachineEventData;
import com.example.events.MachineScheduledEvent;
import com.example.events.MachineScheduledEventData;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@NamedQueries({
        @NamedQuery(name = "Machine.findAll",
                query = "Select m from Machine m"),
        @NamedQuery(name = "Machine.findAllMachineId",
                query = "Select m.machineId from Machine m"),
        @NamedQuery(name = "Machine.findByMachineId",
                query = "Select m from Machine m where m.machineId = ?1"),
        @NamedQuery(name = "Machine.findJobByJobNumber",
                query = "SELECT j FROM Machine m JOIN m.jobList.jobs j WHERE j.jobNumber = :jobNumber"),
        @NamedQuery(name = "Machine.findJobInfoByJobNumber",
                query = "SELECT j FROM Machine m JOIN m.jobList.jobs j WHERE j.jobNumber = :jobNumber"),
        @NamedQuery(name = "Machine.findAllCustomerJobsByCustomerName",
                query = "SELECT j FROM Machine m JOIN m.jobList.jobs j WHERE j.customerName = :customerName")})

public class Machine extends AbstractAggregateRoot<Machine> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private MachineId machineId; // Aggregate Identifier - machine Name
    //@Embedded
    //private Employee employee; //Employee assigned to the Machine
    @Embedded
    private JobList jobList; //List of Jobs assigned to the Machine
    @Embedded
    private Schedule schedule; //Schedule of the Machine

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
        this.machineId = new MachineId(scheduleMachineCommand.getMachineName());
        //this.employee = new Employee(scheduleMachineCommand.getEmployeeName());
        this.jobList = JobList.EMPTY_LIST; //Empty Job List since the Machine has no jobs assigned yet
        this.schedule = new Schedule(); //Empty Schedule since the Machine has no jobs scheduled yet

        // Registering the Machine Scheduled Event
        addDomainEvent(new
                MachineScheduledEvent(
                new MachineScheduledEventData(machineId.getMachineId())
        ));
    }

    public MachineId getMachineId() {
        return machineId;
    }

    public JobList getJobList() {
        return jobList;
    }

    public Schedule getSchedule() {
        // return a defensive copy so callers can't share the internal collection instance
        return (this.schedule == null) ? new Schedule() : new Schedule(this.schedule.getJobs());
    }

    public void setSchedule(Schedule schedule) {
        // defensive copy on assignment to avoid sharing collections
        this.schedule = (schedule == null) ? new Schedule() : new Schedule(schedule.getJobs());
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
                        this.machineId.getMachineId(),
                        job.getJobNumber(),
                        job.getJobTimeNeededDays(),
                        job.getPriority(),
                        job.getDueDate(),
                        job.getMaterialNeeded(),
                        job.getMaterialAmount(),
                        job.getCustomerName())));
    }

    /**
     * Method to register the event
     * @param event
     */
    public void addDomainEvent(Object event){
        registerEvent(event);
    }

    @Override
    public String toString() {
        return "Machine " + machineId + ": " +
                "\njobList=" + jobList +
                "\nschedule=" + schedule;
    }
}
