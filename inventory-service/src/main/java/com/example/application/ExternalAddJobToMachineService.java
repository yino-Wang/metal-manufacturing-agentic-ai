package com.example.application;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.valueobjects.Job;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalAddJobToMachineService {

    //MaterialsNeeded is the class you create - see comments below
    public MaterialsNeeded fetchJobMaterialSpecifications(Job job) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8787/addJobToMachine/findCurrentJobBySchedulingId";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("schedulingId",
                        job.getJobId());  //this will not work yet, will maybe do this so that it returns all current jobs
        //and you don't need to pass in any parameters, or just gets all the top ones
        //you should be able to make the rest of this logic work - it just won't run because of my end
        //will try and do this tomorrow


        // The getForObject method is used to make a GET request to the routing service
        Machine machine = restTemplate.getForObject(builder.toUriString(), Machine.class);
        assert machine != null;
        List<Job> jobs = new ArrayList<>(machine.getJobList().getJobs());
        //this will return a list of jobs assigned to all machines (hopefully)
        //you can then go through the list to get the materialNeeded (name) and materialAmount (quantity needed)


        //maybe look into map (i believe javs's dic is called map) so you can iterate through the list and
        //add the material quantity to the name (like keep updating it if that's a thing)
        Dictionary<>
        for (Job j : jobs) {

        }

        //you would then pass this dictionary as a variable in constructing a class of your choice, that would
        //probably be stored in your main aggregate
        //i.e. MaterialsNeeded class only stores the dictionary you can then access in your microservice
        //as long as an instance of MaterialsNeeded is stored in your main aggregate, it will be saved in the
        //main aggreate's repository
        return new MaterialsNeeded(Dictionary);
    }
}
