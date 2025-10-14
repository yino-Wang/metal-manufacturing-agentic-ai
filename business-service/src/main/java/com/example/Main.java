package com.example;

import com.example.interfaces.rest.dto.AddJobToMachineResource;
import com.example.interfaces.rest.dto.ScheduleMachineResource;
import com.example.interfaces.rest.dto.SchedulingIdDto;
import org.springframework.boot.autoconfigure.SpringBootApplication;  //ADD THIS
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

@SpringBootApplication //ADD THIS
public class Main {
    public static void main(String[] args) throws InterruptedException {
        org.springframework.boot.SpringApplication.run(Main.class, args); //ADD THIS BUT CHANGE *MAIN* TO WHATEVER YOUR FILE IS CALLED


        final String url = "http://localhost:8787/machinescheduling";
        //schedule some machines
        RestTemplate restTemplate = new RestTemplate();
        ScheduleMachineResource machine1 = new ScheduleMachineResource("machine1", "John Smith");
        SchedulingIdDto schedulingId1 = restTemplate.postForObject(url, machine1, SchedulingIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId1);
        ScheduleMachineResource machine2 = new ScheduleMachineResource("machine2", "Percy Waterman");
        SchedulingIdDto schedulingId2 = restTemplate.postForObject(url, machine2, SchedulingIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId2);
        ScheduleMachineResource machine3 = new ScheduleMachineResource("machine3", "Rebecca Castle");
        SchedulingIdDto schedulingId3 = restTemplate.postForObject(url, machine3, SchedulingIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId3);
        ScheduleMachineResource machine4 = new ScheduleMachineResource("machine4", "Taz Lou");
        SchedulingIdDto schedulingId4 = restTemplate.postForObject(url, machine4, SchedulingIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId4);

        //while true keep adding jobs to the machines
        final String urlAddJob = "http://localhost:8787/addJobToMachine";
        Random rand = new Random();
        int maxMaterials = 50;
        String[] machines = {"machine1", "machine2", "machine3", "machine4"};
        int numMachines = machines.length;
        String[] materialNeeded = {"steel", "wood", "nails", "iron"};
        int materialOptions = materialNeeded.length;
        LocalDate day = LocalDate.now();
        int jobNumber = 0;
        while (true) {
            jobNumber = jobNumber + 1;
            int materialAmount = rand.nextInt(maxMaterials) + 1; //at least 1 material
            int r1 = rand.nextInt(numMachines);
            String machineId = machines[r1];
            int material = rand.nextInt(materialOptions);
            String materialName = materialNeeded[material];
            LocalDate submitDate = day.plusDays(rand.nextInt(10)); //submit date within the next 10 days
            System.out.println("Adding job " + jobNumber + " to " + machineId + " for " + materialAmount + " of " + materialName + " on " + submitDate);
            AddJobToMachineResource job = new AddJobToMachineResource(jobNumber, machineId, submitDate, materialName, materialAmount);
            System.out.println("Posting job: " + job.toString());
            SchedulingIdDto schedulingId = restTemplate.postForObject(urlAddJob, job, SchedulingIdDto.class);
            System.out.println("******" + schedulingId + job + "*****");
            System.out.println(job);
            Thread.sleep(1000);
        }
    }
}