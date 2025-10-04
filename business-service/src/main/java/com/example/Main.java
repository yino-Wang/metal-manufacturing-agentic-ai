package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;  //ADD THIS
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

import com.example.infrastructure.repository.EmployeeRepository;
import com.example.model.Employee;


@SpringBootApplication //ADD THIS
public class Main {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Main.class, args); //ADD THIS BUT CHANGE *MAIN* TO WHATEVER YOUR FILE IS CALLED
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }


}