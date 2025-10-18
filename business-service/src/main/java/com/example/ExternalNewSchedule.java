package com.example;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.valueobjects.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExternalNewSchedule {

    //NewSchedule is the class you create - see comments below
    public YourClass fetchNewSchedule() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8787/machinescheduling/findAllMachines"; //call function to get list of all machines
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url); //no params to pass in, just want all machines


        //exchange to handle generic list type
        ResponseEntity<List<Machine>> response = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET, null, // No request entity for a GET request.
                new ParameterizedTypeReference<List<Machine>>() {} //want to return a list of objects, not just a list
        );
        List<Machine> machines = response.getBody();

        //create a dictionary/map, store a machineId with its job schedule
        //i.e. {{machine1, List<job>}, {machine2, List<job>}}
        assert machines != null;
        Map<String, List<Job>> scheduleMap = new HashMap<>();
        for (Machine machine : machines ) {
            scheduleMap.put(machine.getMachineId().getMachineId(), machine.getSchedule().getJobs());
        }


        //add the multiple schedules to a value object in your MS (or directly in your aggregate)
        // either way as long as it is connected to your MAIN AGGREGATE, it will be saved in that repositiory
        return new YourClass(scheduleMap);
    }
}

}
