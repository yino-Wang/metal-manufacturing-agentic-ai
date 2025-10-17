package com.example.application;

import com.example.domain.commands.AddJobMaterialsCommand;
import jakarta.transaction.Transactional;

import java.util.UUID;

public class AllocateMaterialsCommandService {

    private YourRepository yourRepository;


    //put constructor - not empty one

    /**
     * Service Command method to assign a tracking id to the booked cargo
     * @return Tracking Number of the Cargo
     */
    @Transactional
    public void allocateMaterials(AddJobMaterialsCommand addJobMaterialsCommand){
        yourClass.setJobNumber(addJobMaterialsCommand.getJobNumber());
        yourClass.setMaterialName(addJobMaterialsCommand.getMaterialName());
        yourClass.setMaterialAmount(addJobMaterialsCommand.getMaterialAmount());
        //you will need to create a class, you can then add these too
        //you could find the right material by name and add/minus the amount or something
        //either way you have access to the most recent jobs number, material name and amount this way
        //like:
        Material (need to be a class) material = materialRepository.findByMaterialName( new MaterialName(addJobMaterialsCommand.getMaterialName()));
        // ^ MaterialName must be an aggregate identifier - look at my MS with Machine and MachineId

        yourRepository.save(material); //Save the new material number, if that's how you are doing it
        //i assume this can be changed to just updating the total material number then saving
    }

    //YOU WILL NEED TO MOVE THIS-------------------------------------------------------------------------------------------------------------------------
    //to the body of whatever class you end up using. With the current code above that would be a Material aggregate
    //i.e. whatever you are creating and saving
    //Material(THE CLASS YOU CREATE AN OBJECT OF HERE) material = materialRepository.findByMaterialName( new MaterialName(addJobMaterialsCommand.getMaterialName()));

    //        yourRepository.save(material <- AND THE THING YOU SAVE HERE);
    /**
     * Add a job event to the Material Details
     * @param addJobMaterialsCommand
     */
    public void addJobMaterialDetails(AddJobMaterialsCommand addJobMaterialsCommand){
        //trackingEvent is a value object
        TrackingEvent trackingEvent = new TrackingEvent( //just adding all the values needed for trackingEvent here ->
                new TrackingVoyageNumber(addJobMaterialsCommand.getVoyageNumber()),
                new TrackingLocation(addJobMaterialsCommand.getLocation()),
                new TrackingEventType(addJobMaterialsCommand.getEventType(),addJobMaterialsCommand.getEventTime()));
        //updating the 'material' class attribute 'trackingActivityEvent'
        //for you, this might just look like changing int totals of material amounts
        this.trackingActivityEvent.getTrackingEvents().add(trackingEvent);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------



}
