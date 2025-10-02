package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController {

    @GetMapping
    public List<Employee> getEmployees() {
        return List.of(
                new Employee(1, "John", "hi, something, another thing"),
                new Employee(2, "Beth", "hi, something, another thing")
        );
    }
}
