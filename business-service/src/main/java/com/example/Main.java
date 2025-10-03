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

    @Bean
    public CommandLineRunner loadDatabase(BookingRepository bookingRepository,
                                          CustomerRepository customerRepository) throws Exception {
        return args -> {
            Customer customer1 = new Customer("John", "Doe");
            Booking booking1 = new Booking("MS-777",
                    LocalDate.of(2025, 12, 13),
                    LocalDate.of(2025, 12, 31),
                    customer1);
            customerRepository.save(customer1);
            System.out.println(customerRepository.findById(customer1.getCustomerId()).orElseThrow());
            bookingRepository.save(booking1);
            System.out.println(bookingRepository.findById(booking1.getBookingNumber()).orElseThrow());

            Customer customer2 = new Customer("David", "Smith");
            Booking booking2 = new Booking("AB-123",
                    LocalDate.of(2025, 9, 10),
                    LocalDate.of(2025, 9, 14),
                    customer2);
            customerRepository.save(customer2);
            System.out.println(customerRepository.findById(customer2.getCustomerId()).orElseThrow());
            bookingRepository.save(booking2);
            System.out.println(bookingRepository.findById(booking2.getBookingNumber()).orElseThrow());
        };
    }

}