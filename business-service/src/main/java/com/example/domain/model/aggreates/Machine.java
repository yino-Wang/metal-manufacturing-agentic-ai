package com.example.domain.model.aggreates;

import com.example.domain.model.commands.ScheduleMachineCommand;
import com.example.domain.model.commands.AddJobToMachineScheduleCommand;
import com.example.domain.model.entities.Employee;
import com.example.domain.model.valueobjects.CurrentJob;
import com.example.domain.model.valueobjects.JobList;
import com.example.domain.model.valueobjects.LastJobHandledEvent;
import com.example.domain.model.valueobjects.Schedule;
import com.example.interfaces.rest.MachineJobScheduledEventData;
import com.example.interfaces.rest.MachineScheduledEvent;
import com.example.interfaces.rest.MachineScheduledEventData;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@NamedQueries({
        @NamedQuery(name = "Machine.findAll",
                query = "Select m from Machine m"),
        @NamedQuery(name = "Machine.findByBookingId",
                query = "Select m from Machine m where m.schedulingId = ?1"),
        @NamedQuery(name = "Machine.findAllMachineScheduleIds",
                query = "Select m.bookingId from Machine m") })
public class Machine extends AbstractAggregateRoot<Machine> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
        this.employee = new Employee(scheduleMachineCommand.getEmployeeName());
        this.jobList = JobList.EMPTY_LIST; //Empty Job List since the Machine has no jobs assigned yet
        this.schedule = new Schedule(); //Empty Schedule since the Machine has no jobs scheduled yet

        // Registering the Machine Scheduled Event
        addDomainEvent(new
                MachineScheduledEvent(
                new MachineScheduledEventData(schedulingId.getSchedulingId(),
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
     * @param jobList
     */

    public void assignJob(JobList jobList) {
        this.jobList = jobList;
        //Add this domain event which needs to be fired when the new jobList is saved
        addDomainEvent(new
                AddJobToMachineScheduleCommand(new MachineJobScheduledEventData(this.schedulingId.getSchedulingId())));
    }

    /**
     *
     * @param lastJobHandledEvent
     */
    public void deriveCurrentJobProgress(LastJobHandledEvent lastJobHandledEvent) {
        this.currentJob = CurrentJob.derivedFrom(getJobList(),
                lastJobHandledEvent);
    }


    /**
     * Method to register the event
     * @param event
     */
    public void addDomainEvent(Object event){
        registerEvent(event);
    }

}
