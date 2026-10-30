package com.groceryai.backend.controller;

import com.groceryai.backend.dto.EmployeeResponse;
import com.groceryai.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/city/{cityId}")
    public List<EmployeeResponse> getEmployeesByCity(
            @PathVariable Integer cityId) {
        return employeeService.getEmployeesByCity(cityId);
    }

    @GetMapping("/{id:\\d+}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }
}