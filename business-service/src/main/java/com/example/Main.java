package com.example;

//import com.example.infrastructure.agentic.ModelLogger;
import com.example.application.agentService.ScheduleService;
import com.example.application.service.SchedulingService;
import com.example.domain.model.aggreates.Machine;
import com.example.domain.model.aggreates.MachineId;
import com.example.domain.model.valueobjects.Schedule;
import com.example.infrastructure.repositories.MachineRepository;
import com.example.interfaces.rest.dto.AddJobToMachineResource;
import com.example.interfaces.rest.dto.MachineIdDto;
import com.example.interfaces.rest.dto.ScheduleDto;
import com.example.interfaces.rest.dto.ScheduleMachineResource;
//import dev.langchain4j.model.chat.listener.ChatModelListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;  //ADD THIS
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication //ADD THIS
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(Main.class, args); //ADD THIS BUT CHANGE *MAIN* TO WHATEVER YOUR FILE IS CALLED

        ScheduleService service = context.getBean(ScheduleService.class);

        final String url = "http://localhost:8787/machinescheduling";
        //schedule some machines
        RestTemplate restTemplate = new RestTemplate();
        ScheduleMachineResource machine1 = new ScheduleMachineResource("machine1");
        MachineIdDto schedulingId1 = restTemplate.postForObject(url, machine1, MachineIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId1);
        ScheduleMachineResource machine2 = new ScheduleMachineResource("machine2");
        MachineIdDto schedulingId2 = restTemplate.postForObject(url, machine2, MachineIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId2);
        ScheduleMachineResource machine3 = new ScheduleMachineResource("machine3");
        MachineIdDto schedulingId3 = restTemplate.postForObject(url, machine3, MachineIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId3);
        ScheduleMachineResource machine4 = new ScheduleMachineResource("machine4");
        MachineIdDto schedulingId4 = restTemplate.postForObject(url, machine4, MachineIdDto.class);
        System.out.println("Scheduled machine: " + schedulingId4);

        //while true keep adding jobs to the machines
        final String urlAddJob = "http://localhost:8787/addJobToMachine";
        Random rand = new Random();
        int maxMaterials = 50;
        int maxDays = 30;
        String[] machines = {"machine1", "machine2", "machine3", "machine4"};
        int numMachines = machines.length;
        String[] materialNeeded = {"steel", "wood", "nails", "iron"};
        int materialOptions = materialNeeded.length;
        String[] customers = {"Michelle", "Randy", "Rob", "Deb"};
        int numCustomers = customers.length;
        int[] priorityOptions = {1, 2, 3, 4, 5};
        int priorityNum = priorityOptions.length;
        LocalDate day = LocalDate.now();
        int jobNumber = 0;
        while (true) {
            jobNumber = jobNumber + 1;
            int materialAmount = rand.nextInt(maxMaterials) + 1; //at least 1 material
            int jobTimeNeededDays = rand.nextInt(maxDays) + 1; //at least 1 day needed
            int r1 = rand.nextInt(numMachines);
            String machineId = machines[r1];
            int material = rand.nextInt(materialOptions);
            String materialName = materialNeeded[material];
            int customer = rand.nextInt(numCustomers);
            String customerName = customers[customer];
            int priorityChosen = rand.nextInt(priorityNum);
            int priority = priorityOptions[priorityChosen];
            LocalDate dueDate = day.plusDays(rand.nextInt(10)); //submit date within the next 10 days
            System.out.println("Adding job " + jobNumber + " to " + machineId + " for " + materialAmount + " of " + materialName + " on " + dueDate);
            AddJobToMachineResource job = new AddJobToMachineResource(jobNumber, jobTimeNeededDays, priority, machineId, dueDate, materialName, materialAmount, customerName);
            //System.out.println("Posting job: " + job.toString());
            MachineIdDto schedulingId = restTemplate.postForObject(urlAddJob, job, MachineIdDto.class);
            //System.out.println("******" + schedulingId + job + "*****");
            //System.out.println(job);

            ///create fake schedule
            Schedule schedule = service.generateSchedule(machineId);

            //ScheduleDto scheduleDto = restTemplate.postForObject(urlAddJob, schedule, ScheduleDto.class);

            Thread.sleep(3000);
        }
    }

//    @Bean
//    ChatModelListener chatModelLogger() {return new ModelLogger();}
}